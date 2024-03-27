from youtube_download import youtube_download
from pydub import AudioSegment
import subprocess
import pandas as pd
import os

AudioSegment.converter ="./.venv/ffmpeg/bin/ffmpeg.exe"

AudioSegment.ffprobe = "./.venv/ffmpeg/bin/ffprobe.exe"

links = pd.read_csv("./youtubeLink.csv")
for data in links.values:
    
    title,channel,url = data[0], data[1],data[2]
    result = youtube_download(title, url)
    print(title)
    if result:
        command = ["spleeter", "separate", "-p", "spleeter:5stems","-o", "music", f"./music/{title}"]
        subprocess.run(command, shell=True)
        os.remove(f"./music/{title}.mp3")

# from pydub.utils import mediainfo
# from pathlib import Path
#     def mp3_to_wav(mp3_file, wav_file):
#     sound = AudioSegment.from_mp3(mp3_file)
#     sound.export(wav_file, format="wav")

# if __name__ == "__main__":
#     mp3_file = Path("./haeyo.mp3")
#     wav_file = "output_wav_file.wav"
#     mp3_to_wav(mp3_file, wav_file)