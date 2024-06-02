from pydantic import BaseModel
from fastapi import FastAPI
import ExtractYoutube
import subprocess
import datetime
import time
import pandas as pd
import asyncio
import shutil
import os

app = FastAPI()

TODAY = lambda: datetime.datetime.now().strftime('%Y%m%d')
NOW = lambda: datetime.datetime.now().strftime('%H%M%S')

class Item(BaseModel):
    file_link: str
    target_pitch: str

class LinkItem(BaseModel):
    youtubeUrl: str
    userPitch : float

class CheckItem(BaseModel):
    musicPitch: float
    userPitch : float

class youtubeLinkItem(BaseModel):
    youtubeUrl : str

# 싱글톤 객체 생성
extract_youtube = ExtractYoutube.ExtractYoutube()
@app.get('/')
async def root():
    return {"message" : "test"}

@app.post("/range_test/")
async def range_test(item: Item):
    '''
        사용자가 음성 데이터를 받아서 처리 후 결과값 반환
    '''
    try:
        now_time = NOW()
        user_voice_link = f"./user/{now_time}.mp3"
        wav_file = f"./user/{now_time}.wav"
        okay= False

        extract_youtube.downloadS3(file_name = user_voice_link, key = item.s3Link)
        subprocess.run(['ffmpeg', '-i', user_voice_link, wav_file], 
                                stdout=subprocess.PIPE, 
                                stderr=subprocess.PIPE, 
                                text=True)
        
        is_valid = extract_youtube.remove_silence(wav_file,wav_file)
        if is_valid:
            accuracy = extract_youtube.estimate_c(wav_file,item.target)
            if accuracy >=80:
                okay = True
            os.remove(user_voice_link)
            os.remove(wav_file)
            return {"accuracy": accuracy, "target" : item.target, "pass" : okay}
        else:
            os.remove(user_voice_link)
            os.remove(wav_file)
            return {"accuracy": 0, "target" : item.target, "pass" : False}
    except ZeroDivisionError as e:
        print(e)
        return {"accuracy": 0, "target" : item.target, "pass" : False}
    except Exception as e:
        print(e)

# @app.post("youtube_url_check")
# async def youtube_validation(item : youtubeLinkItem):
#     item.   youtubeUrl

@app.post("/youtube_extract_check")
async def youtube_extract_check(item : CheckItem):
    current_scale = ''
    key_diff = 0
    pitches = [item.userPitch,item.musicPitch]
    pitches.sort()
    for scale, scope in extract_youtube.note_freq_dict.items():
        if pitches[0] < scope[1] and pitches[0] >= scope[0]:
            current_scale = scale
    index = extract_youtube.note_list.index(current_scale)
    for scope in extract_youtube.scale_list[index:]:
        if pitches[1] < scope[1] and pitches[1] >= scope[0]:
            break
        else: key_diff -=1

    if item.userPitch >= item.musicPitch:
        key_diff *=-1
    return {"keyDiff" : key_diff}

@app.post("/youtube_extract")
async def youtube_extract(item : LinkItem):
    '''
        사용자가 입력한 youtube_link의 음역대를 추출하여 반환한다.
        DB에 저장하기 위한 추가 변환도 수행한다.
    '''
    highest_pitch = 600
    key_diff = 0
    current_scale = ''  
    # total_value = [str(i) for i in range(10)]
    # time.sleep(10)

    try:
        youtube_link = item.youtubeUrl
        total_value = extract_youtube.get_song_info(youtube_link,"user_input")
        song = pd.DataFrame(data = [total_value],columns=["title","link","id","playlist_title","duration","uploader","youtube_downloaded","spleeter","remove_space_wav_to_s3","vocal_range","mfcc_origin","mfcc_after_transform"])
        highest_pitch =extract_youtube.extract_youtube("user_input.csv",song)

        pitches = [item.userPitch,highest_pitch]
        pitches.sort()
        for scale, scope in extract_youtube.note_freq_dict.items():
            if pitches[0] < scope[1] and pitches[0] >= scope[0]:
                current_scale = scale
        index = extract_youtube.note_list.index(current_scale)
        for scope in extract_youtube.scale_list[index:]:
            if pitches[1] < scope[1] and pitches[1] >= scope[0]:
                break
            else: key_diff -=1
        if item.userPitch >= highest_pitch:
            key_diff *=-1
        return {"highPitch": float(highest_pitch), "youtubeUrl": total_value[1],"title" : total_value[0], "youtubeUrlId" : total_value[2],'duration' : total_value[4], 'playlistTitle' : 'user_input',"uploader": total_value[5], "keyDiff": key_diff,"isYoutubeUrl" : True}
    except Exception as e:
        return {"highPitch": 0.0, "youtubeUrl": '',"title" : '', "youtubeUrlId" : '','duration' : 0, 'playlistTitle' : '',"uploader": '', "keyDiff": -1,"isYoutubeUrl" : False}

# @app.post("/vocal_test")
# async def vocal():
#     # youtube_extract(youtube_link)
#     new_name = f"./user_input_collection/{TODAY()}_user_input.csv"
#     os.rename("user_input.csv", new_name)
#     data = pd.read_csv(new_name)
#     extract_youtube.activate_extrawav(new_name,data)
#     return {"message": "completed"}

