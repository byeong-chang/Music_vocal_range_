
from pytube import YouTube
import os 
# import moviepy.editor as mp
def youtube_download(youtube_url):

    # YouTube 객체 생성
    yt = YouTube(youtube_url)

    # 가장 높은 품질의 오디오 스트림 가져오기
    audio_stream = yt.streams.filter(only_audio=True).first()

    # 오디오 스트림 다운로드
    audio_stream.download(output_path='temp', filename='audio')

    # 다운로드한 오디오 파일명
    audio_file = 'temp/audio'
    print(os.getcwd())
    try:
        os.rename(audio_file, audio_file + ".mp3")
    except FileNotFoundError:
        print("파일을 찾을 수 없습니다.")
    except PermissionError:
        print("파일 이름을 변경할 권한이 없습니다.")

    

