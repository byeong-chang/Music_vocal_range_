from fastapi import FastAPI
from youtube_download import youtube_download
from highest_pitch_extraction import extract_highest_pitch
import subprocess
import os


app = FastAPI()

@app.get("/")
async def root():


    test = '''엠씨더맥스,잠시만안녕,https://www.youtube.com/watch?v=ikx0NPc4f5E
엠씨더맥스,어디에도,https://www.youtube.com/watch?v=afYxfsgiuLI
임창정,내가 저지른 사람,https://www.youtube.com/watch?v=kowTrv02LHw
나얼,바람 기억,https://www.youtube.com/watch?v=4MbzvYnQSlw
더 크로스,돈 크라이,https://www.youtube.com/watch?v=A-ZBkJ2Frbw
박재정,헤어지자 말해요,https://www.youtube.com/watch?v=SrQzxD8UFdM
임한별,이별하러 가는 길,https://www.youtube.com/watch?v=K1FRxQfvctw
허각,나를 사랑했던 사람아,https://www.youtube.com/watch?v=vepz3RlTd4M
닐로,지나오다, https://www.youtube.com/watch?v=uqQqnWfJyAA
닐로,벗,https://www.youtube.com/watch?v=trXGsyWlkGM
임재현,사랑에 연습이 있었다면,https://www.youtube.com/watch?v=T8XbVWBEMfw
임재현,조금 취했어,https://www.youtube.com/watch?v=VgbIDfPJIFs
장덕철,그날처럼,https://www.youtube.com/watch?v=v6_GwXU1lkg'''
    lst = []
    for song in test.split('\n'):
        print(song.split("\t"))
        singer,title,link =  song.split(",")
        result = youtube_download(singer, title, link)

        if result:
            command = ["spleeter", "separate", "-p", "spleeter:2stems", "-o", "music", f"./music/{singer}_{title}"]
            subprocess.run(command, shell=True)
            os.remove(f"./music/{singer}_{title}.mp3")
            lst.append(extract_highest_pitch(f"./music/{singer}_{title}.mp3"))

    return {"message": f"{lst}"}