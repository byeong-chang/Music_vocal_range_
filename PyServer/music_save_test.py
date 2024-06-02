import ExtractYoutube
import pandas as pd

et = ExtractYoutube.ExtractYoutube()
data = pd.read_csv("trot.csv")
et.run("trot.csv",data)
et.activate_extrawav("trot.csv",data)
et.sendS3("trot.csv","./data/trot.csv")

data = pd.read_csv("ballad.csv")
et.run("ballad.csv",data)
et.activate_extrawav("ballad.csv",data)
et.sendS3("ballad.csv","./data/ballad.csv")

# et.activate_extrawav("infos.csv")
# et.activate_extrawav()
# et.activate_extrawav()
# et.sendS3("jazz.csv","./data/jazz.csv")
# et.downloadS3("hiphop.csv","./data/hiphop.csv")
# et.downloadS3("jazz.csv","./data/jazz.csv")
# et.downloadS3("trot.csv","./data/trot.csv") 
# et.downloadS3("ballad.csv","./data/ballad.csv")

# data = pd.read_csv("jazz.csv")
# data['vocal_range'] = False
# data['mfcc_after_transform'] = False
# data.to_csv("jazz.csv",index = False)

def activate_extract_youtube_link(link,title):
    # 플레이리스트를 넣으면 링크를 뽑아 csv로 저장

    # for playlist in playlists:
    et = ExtractYoutube()
    et.get_playlist_info(link,title)

def activate_youtube_download():
    # csv파일들을 읽어서 노래 다운
    et = ExtractYoutube()
    infos = pd.read_csv("infos.csv")
    for i in range(len(infos)):
        song = infos.loc[i]
        print("index : " , i)
        if not song['youtube_downloaded']:
            et.download_youtube_video_as_mp3(song['link'],song['title'],song['playlist_title'])
            infos.loc[i, 'youtube_downloaded'] = True
            infos.to_csv("infos.csv",index = False)

def activate_spleeter():
    et = ExtractYoutube()
    infos = pd.read_csv("infos.csv")
    for i in range(len(infos)):
            print(i)
            music = infos.loc[i]
            if not music['spleeter']:
                try :
                    music_title = music['title']
                    playlist_title = music['playlist_title']

                    output_path = f"./spleeter/{playlist_title}/{music_title}"
                    file_path = f"./music/{playlist_title}/{music_title}.mp3"
                    et.calSpleeter(output_path,file_path)

                    infos.loc[i, 'spleeter'] = True
                    infos.to_csv("infos.csv",index = False)
                except Exception as e:
                    print(e)

def activate_remove_space():
    # csv파일들을 읽어서 노래 다운
    et = ExtractYoutube()
    infos = pd.read_csv("infos.csv")

    if not os.path.exists("./removed_space"):
        os.makedirs("./removed_space")

    for i in range(len(infos)):
        song = infos.loc[i]
        if not song['remove_space_wav_to_s3']:
            song_title =song['title']
            playlist_title =song['playlist_title']

            file_path = f"./spleeter/{playlist_title}/{song_title}.wav"
            output_path = f"./removed_space/{playlist_title}/{song_title}.wav"
            if not os.path.exists(f"./removed_space/{playlist_title}"):
                os.makedirs(f"./removed_space/{playlist_title}")

            if os.path.exists(output_path):
                infos.loc[i, 'remove_space_wav_to_s3'] = True
                infos.to_csv("infos.csv",index = False)
                continue

            try:
                print(i)
                et.remove_silence(file_path,output_path)
                infos.loc[i, 'remove_space_wav_to_s3'] = True
                infos.to_csv("infos.csv",index = False)
            except Exception as e:
                print(e,song_title,playlist_title)

def activate_mfcc():
    infos = pd.read_csv("infos_copy.csv")
    et = ExtractYoutube()

    for i in range(len(infos)):
        song = infos.loc[i]
        if not song['mfcc_value']:
            song_title =song['title']
            playlist_title =song['playlist_title']

            file_path = f"./music/{playlist_title}/{song_title}.mp3"
            try:
                print(i)
                mfcc_val = et.extract_mfcc_features(file_path)
                infos.loc[i, 'mfcc_value'] = str(mfcc_val)
                infos.to_csv("infos.csv",index = False)
            except Exception as e:
                print(e,song_title,playlist_title)

def activate_highest_peach():
    infos = pd.read_csv("infos.csv")
    et = ExtractYoutube()     
    for i in range(len(infos)):
        song = infos.loc[i]
        if song['vocal_range'] == "False" or not song['vocal_range']: # boolean으로 지금까지 하다가, False인데 열안에 값이 하나라도 923.1 들어가면 False가 문자열 False로 인식됨
            song_title =song['title']      
            playlist_title =song['playlist_title']
            file_path = f"./removed_space/{playlist_title}/{song_title}.wav"
            try:
                print(i)
                print(file_path)
                vocal_range = et.extract_highest_pitch(file_path)
                infos.loc[i, 'vocal_range'] = vocal_range
                infos.to_csv("infos.csv",index = False)
            except Exception as e:
                print(e)


# activate_youtube_download()
# activate_spleeter()
# activate_remove_space()
# activate_mfcc()
#activate_highest_peach()

# playlist들 url 넣어서 보내면 노래 한곡한곡 뽑는 코드 실행
# playlist_urls = [
#     "https://www.youtube.com/playlist?list=PLkijzJW7zLBVjWGGtcA_Q2Vu8UuG5kLvt",
#     "https://www.youtube.com/playlist?list=PLtodMGvo4t2tkkrAwmErsV3bHbr_hRhvJ",
#     "https://www.youtube.com/playlist?list=PLF0EDAD63736E60B1",
#     "https://www.youtube.com/playlist?list=PL1y4EAbxquZ-a_PCOHdXXwdiAEeaS9VmR",
#     "https://www.youtube.com/playlist?list=PLpcbzk7Z9-ywZBeFYOrDvn_VM76Tvk2yt",
#     "https://www.youtube.com/playlist?list=PL-FVH5VWgRPE_91MmVotuEmBWYED6Gph6",
#     "https://www.youtube.com/playlist?list=PLTDmNT4owFz2pivbgvdIIUR3yYTx6LCY0",
#     "https://www.youtube.com/playlist?list=PLQt-Kwdd_w5F2S9f2jk6cMRbQI3T-I5bK",
#     "https://www.youtube.com/playlist?list=PLHHtv-Y_2jvToAwHzi9ijokpYGL5MFjYM",
#     "https://www.youtube.com/playlist?list=PL5DfzBfnyk4XtJDX2xoEBjP_HTmYmPiCt",
#     "https://www.youtube.com/playlist?list=PLK9vuK1BUmgr8gCPLrTqUH_dwM4r1NsNP",
#     "https://www.youtube.com/playlist?list=PL4brMRtrvP4H3kazafIRbtWkB8GcGFeLu",
#     "https://www.youtube.com/playlist?list=PLTZ4XeC4jx1oxNrCGOGnI4NwvvqFsfQrj",
#     "https://www.youtube.com/playlist?list=PLjo9R7bl95CAjiYZY7q-P-7XF6QmibZdD",
#     "https://www.youtube.com/playlist?list=PLEFDE3F40ACCEF0BE",
#     "https://www.youtube.com/playlist?list=PL5jqJ2fm-QnglMlp3i4CaqkEpyuDr_Wrq",
#     "https://www.youtube.com/playlist?list=PL3WsJs37OJFMCf6QT-5_CtFTWGfZ4KUDG",
#     "https://www.youtube.com/playlist?list=PL5gOt5XOe6KCmxfMiejAtwz5kiYB3xYWP"
# ]

# for playlist in playlist_urls:
#     activate_extract_youtube_link(playlist,"hiphop")