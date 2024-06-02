from pydantic import BaseModel
from fastapi import FastAPI
import ExtractYoutube
import subprocess
import datetime
import pandas as pd
import shutil
import os

app = FastAPI()

TODAY = lambda: datetime.datetime.now().strftime('%Y%m%d')
NOW = lambda: datetime.datetime.now().strftime('%H%M%S')

class Item(BaseModel):
    file_link: str
    target_pitch: str

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
    now_time = NOW()
    user_voice_link = f"./user/{now_time}.wav"
    extract_youtube.downloadS3(file_name = user_voice_link, key = item.file_link)
    extract_youtube.remove_silence(user_voice_link,user_voice_link)
    correct_percentage = extract_youtube.estimate_c(user_voice_link,item.target_pitch)

    return {"correct_percentage": correct_percentage}

@app.post("/youtube_extract/")
async def youtube_extract(item : Item):
    '''
        사용자가 입력한 youtube_link의 음역대를 추출하여 반환한다.
        DB에 저장하기 위한 추가 변환도 수행한다.
    '''
    youtube_link = item.file_link
    total_value = extract_youtube.get_song_info(youtube_link,"user_input")
    song = pd.DataFrame(data = [total_value],columns=["title","link","id","playlist_title","duration","uploader","youtube_downloaded","spleeter","remove_space_wav_to_s3","vocal_range","mfcc_origin","mfcc_after_transform"])
    highest_pitch =extract_youtube.run("user_input.csv",song)
    return {"highest_pitch": str(highest_pitch)}
    
@app.post("/range_test/")
async def music_to_s3():
    '''
        youtube_link를 전체 처리한 후, s3에 저장 및 database에 저장할 수 있는 값 반환
    '''
    # youtube_extract(youtube_link)
    new_name = f"./user_input_collection/{TODAY()}_user_input.csv"
    os.rename("user_input.csv", new_name)
    data = pd.read_csv(new_name)
    extract_youtube.activate_extrawav(new_name,data)
    return {"message": "completed"}