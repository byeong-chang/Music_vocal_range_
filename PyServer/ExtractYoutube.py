from pytube.innertube import _default_clients
from googleapiclient.discovery import build
from pydub.silence import split_on_silence
import scipy.io.wavfile as wavfile
from pydub import AudioSegment
import urllib.parse as urlparse
from pytube import YouTube
import soundfile as sf
import pandas as pd
import numpy as np
import subprocess      
import warnings
import librosa
import music21
import yt_dlp
import shutil
import scipy
import boto3
import aubio
import glob
import time
import os 
import re


class ExtractYoutube():

    def __init__(self):
        # self.youtube = build('youtube', 'v3', developerKey=self.youtube_api_key)
        self.music_links = 'link_in_playlist'
        self.log_datas = pd.DataFrame(columns = ["title","uploader","link","error"])
        self.errors = pd.DataFrame(columns = ["title","video_url","error"])

        # s3 설정들
        apikey = self.get_apikey()
        if apikey:
            self.access_key,self.secret_key = apikey[0],apikey[1]
        else: return

        self.s3 = boto3.client('s3',
                    aws_access_key_id=self.access_key,
                    aws_secret_access_key=self.secret_key,
                    region_name="ap-northeast-2"
                    )
    
        self.scale_list = []
        # th가 작아질수록 음역대 테스트가 쉬워짐
        scale_th = 19
        self.note_list = [
             'C3', 'CSharp3', 'D3', 'DSharp3', 'E3', 'F3', 'FSharp3', 'G3', 'GSharp3', 'A3', 'ASharp3', 'B3',
             'C4', 'CSharp4', 'D4', 'DSharp4', 'E4', 'F4', 'FSharp4', 'G4', 'GSharp4', 'A4', 'ASharp4', 'B4',
             'C5', 'CSharp5', 'D5', 'DSharp5', 'E5', 'F5', 'FSharp5', 'G5', 'GSharp5','A5', 'ASharp5']
        scale_list = [130.81, 138.59, 146.83, 155.56, 164.81, 174.61, 185.00, 196.00, 207.65, 220.00, 233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23,
        369.99, 392.00, 415.30, 440.00, 466.16, 493.88, 523.25, 554.37, 587.33, 622.25, 659.26, 698.46, 739.99, 783.99, 830.61, 880.00]
        
        for scale in scale_list:
            self.scale_list.append([scale-scale/scale_th,scale+scale/scale_th])

        scale_list = [[32.7, 34.65], [34.65, 36.71], [36.71, 38.89], [38.89, 41.20], [41.20, 43.65], [43.65, 46.25], [46.25, 49.00], [49.00, 51.91], [51.91, 55.00], [55.00, 58.27], [58.27, 61.74], [61.74, 65.41],
                    [65.41, 69.30], [69.30, 73.42], [73.42, 77.78], [77.78, 82.41], [82.41, 87.31], [87.31, 92.50], [92.50, 98.00], [98.00, 103.83], [103.83, 110.00], [110.00, 116.54], [116.54, 123.47], [123.47, 130.81],
                    [130.81, 138.59], [138.59, 146.83], [146.83, 155.56], [155.56, 164.81], [164.81, 174.61], [174.61, 185.00], [185.00, 196.00], [196.00, 207.65], [207.65, 220.00], [220.00, 233.08], [233.08, 246.94], [246.94, 261.63],
                    [261.63, 277.18], [277.18, 293.66], [293.66, 311.13], [311.13, 329.63], [329.63, 349.23], [349.23, 369.99], [369.99, 392.00], [392.00, 415.30], [415.30, 440.00], [440.00, 466.16], [466.16, 493.88], [493.88, 523.25],
                    [523.25, 554.37], [554.37, 587.33], [587.33, 622.25], [622.25, 659.26], [659.26, 698.46], [698.46, 739.99], [739.99, 783.99], [783.99, 830.61], [830.61, 880.00], [880.00, 932.33], [932.33, 987.77], [987.77, 1046.50]]

        self.note_freq_dict = dict(zip(note_list, scale_list))
        os.makedirs("./user", exist_ok=True)

    def download_youtube_video_as_mp3(self, video_url, video_title,output_path):
        try:
            ydl_opts = {
                'format': 'worstaudio',
                'postprocessors': [{
                    'key': 'FFmpegExtractAudio',
                    'preferredcodec': 'mp3',
                    'preferredquality': '192',
                }],
                'ignoreerrors': True,
                'outtmpl': f'./music/{output_path}/{video_title}.%(ext)s'
            }

            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                ydl.download([video_url])
            return True
        except yt_dlp.utils.DownloadError as e:
            return False

    def youtubePlaylistDownload(self,playlist_link, output_dir="music"):
        try:
            os.makedirs(output_dir, exist_ok=True)

            ydl_opts = {
                'ignoreerrors': True,
                'dumpjson': True,
                'extractaudio': True,
                'audioformat': 'mp3',
                'audioquality': 0,
                'outtmpl': f'{output_dir}/%(playlist_index)s - %(title)s.%(ext)s'
            }

            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                video_info = ydl.extract_info(playlist_link, download=False)

                for entry in video_info['entries']:
                    title = entry['title']
                    video_url = entry['id']
                    self.playlist_datas.loc[len(self.log_datas)] = [title,video_url]
                    print(f"Downloaded: {title}")

            return True
        except Exception as e:
            print(f"Error downloading playlist: {e}")
            return False

    def get_playlist_info(self,playlist_url,playlist_name):
        
        if os.path.exists(f"{playlist_name}.csv"):
            self.playlist_datas = pd.read_csv(f"{playlist_name}.csv")
        else:
            self.playlist_datas = pd.DataFrame(columns=["title","link","id","playlist_title","duration","uploader","youtube_downloaded","spleeter","remove_space_wav_to_s3","vocal_range","mfcc_origin","mfcc_after_transform"])
        playlist_name = self.remove_special_characters(playlist_name)
        ydl_opts = {
            'extract_flat': True,  # 모든 정보를 평면적으로 가져오기
            'quiet': True,  # 로그 출력 억제
            "ignoreerrors" : False
        }
        try:
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                playlist_info = ydl.extract_info(playlist_url, download=False)
                # 플레이리스트 내의 각 동영상 정보 출력
                for video in playlist_info['entries']:
                    
                    self.title = self.remove_special_characters(video['title'])
                    self.url = video['url']
                    self.video_id = video['id']
                    self.duration = video['duration']
                    self.uploader = video['uploader']
                    total_value = [self.title,self.url,self.video_id,playlist_name,self.duration,self.uploader,False,False,False,False,False,False]
                    self.playlist_datas.loc[len(self.playlist_datas)] = total_value
                self.playlist_datas.to_csv(f"{playlist_name}.csv",index = False)
                return total_value
        except Exception as e:
            print(e, "inappropriate youtube_link")
            return f"{e} occured in downlaoding youtube"

        # self.playlist_datas.to_csv("test.csv",index = False)
   
    def get_song_info(self, video_url, playlist_name):

        if os.path.exists(f"{playlist_name}.csv"):
            self.playlist_datas = pd.read_csv(f"{playlist_name}.csv")
        else:
            self.playlist_datas = pd.DataFrame(columns=["title","link","id","playlist_title","duration","uploader","youtube_downloaded","spleeter","remove_space_wav_to_s3","vocal_range","mfcc_origin","mfcc_after_transform"])
        
        ydl_opts = {
            'quiet': True,  # 로그 출력 억제
            "ignoreerrors": False
        }
        try:
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                video_info = ydl.extract_info(video_url, download=False)
                # 단일 동영상 정보 출력
                self.title = self.remove_special_characters(video_info['title'])
                self.url = video_url
                self.video_id = video_info['id']
                self.duration = video_info['duration']
                self.uploader = video_info['uploader']
                total_value = [self.title, self.url, self.video_id, playlist_name, self.duration, self.uploader, False, False, False, False, False, False]
                self.playlist_datas.loc[len(self.playlist_datas)] = total_value
                return total_value
        except Exception as e:
            print(e, "inappropriate youtube_link")
            return f"{e} occured in downloading youtube"


    def calSpleeter(self,output_path,file_path, quality = str(2)):
        output_path = output_path[:-4]
        playlist_path = os.path.join(*output_path.split("/")[:3])
        command = ["python3", "-m", "spleeter", "separate", "-p", f"spleeter:{quality}stems", "-o", playlist_path, file_path]
        subprocess.run(command, check=True)    
        shutil.move(f'{output_path}/vocals.wav', f'{output_path}.wav')
        shutil.rmtree(output_path)

    def remove_silence(self,file_path,output_path):
        # Load the audio file
        audio = AudioSegment.from_wav(file_path)
        # Split on silence: this will return a list of audio segments
        segments = split_on_silence(audio, min_silence_len=1000, silence_thresh=-40)
        # Concatenate all the segments
        output = sum(segments)
        # Export the result
        print(output)
        output.export(output_path, format="wav")
        file_path = file_path.replace("spleeter","music")
        print(file_path)
        # self.sendS3(output_path,file_path)

    def extract_mfcc_features(self, file_path):
        # wav 파일 로드
        y, sr = librosa.load(file_path)
        # MFCC 추출
        mfcc = librosa.feature.mfcc(y=y, sr=sr)
        # 평균 MFCC 계산
        avg_mfcc = np.mean(mfcc, axis=1)
        # 결과 반환
        return avg_mfcc

    def extract_highest_pitch(self,file_path, time_interval=0.1, volume_thresh=0.001): # time_interval[출력 시간 간격], volume_thresh[볼륨 임계값]


        # 파일의 샘플링 레이트 확인
        with sf.SoundFile(file_path) as f:
            samplerate = f.samplerate

        # PCM 형식으로 저장된 WAV 파일 읽기
        pDetection = aubio.pitch("default", 2048, 2048//2, samplerate)
        pDetection.set_unit("Hz")
        pDetection.set_silence(-20)

        # aubio.source에 PCM 형식의 오디오 데이터 전달
        src = aubio.source(file_path, samplerate, 2048//2)
        # # PCM 형식으로 저장된 WAV 파일 읽기
        # pDetection = aubio.pitch("default", 2048, 2048//2, 44100)       # aubio.pitch : 주파수(음높이)감지 객체 생성 코드
                                                                          # ("default", 2048, 2048//2, 44100)은 각각 감지 알고리즘, 버퍼 사이즈, 버퍼 간격 사이즈, 샘플링 레이트인데 기본값 사용해서 안중요함
        # pDetection.set_unit("Hz")                                       # set_unit : 주파수 감지 객체의 단위를 Hz로 정한 거임
        # pDetection.set_silence(-20)                  # 소리 감지 임계값을 -40dB로 설정(** 이걸로 삐슝빠슝 없앨 수 있긴한데 노래마다 다를거같아서 못건드리고 있음. 서버 열어서 돌려보고 판단)
        
        # # aubio.source에 PCM 형식의 오디오 데이터 전달
        # src = aubio.source(file_path, 44100, 2048//2)    # 오디오 파일 가져오는 코드(liborsa의 
        total_frames = 0                                 # 현재까지 재생한 프레임 위치(시간) 축적 변수
        filtered_pitches = {"time" : [], "value" : []}
        one_block = {"start" : - 1 , "end" : -1 , "data" : []}
        threshold_hz = 100
        threshold_time = 0.5

        while True:
            samples, read = src()                             # samples : 버퍼 샘플 데이터 배열 / read : 버퍼 사이즈
            pitch = pDetection(samples)[0]                    # samples[0]은 감지된 주파수 / samples[1]은 감지된 주파수의 신뢰성(** 이거 threashold 잡아서 이상치 처리도 가능해 보이긴 함)
            volume = np.sum(samples**2) / len(samples) * 100  # volume 구하는건데 이건 그냥 전자기학적 공식

            if pitch and volume > volume_thresh:              # 감지된 주파수가 있고 and 그 주파수가 볼륨 임계값 보다 큰 경우,
                current_pitch = music21.pitch.Pitch()         # 현재의 주파수를 기준으로 해서 music21객체 생성
                current_pitch.frequency = pitch               # Hz를 음계로 변환하기 위해서 현재 주파수를 music21 객체의 picth로 설정

                ########################## 사람 음성 최저 음역대와 최고 음역대로 범위 설정 ##########################
                if 80 <= pitch <= 830:
                    if len(one_block['data']) == 0:
                        one_block['start'] = total_frames/src.samplerate
                        one_block['end'] = total_frames/src.samplerate
                        one_block['data'].append(pitch)
                    else:
                        if one_block['data'][0] + threshold_hz < pitch or one_block['data'][0] - threshold_hz > pitch:

                            if one_block['end'] - one_block['start'] > threshold_time:
                                filtered_pitches['value'].append(max(one_block['data']))
                                filtered_pitches['time'].append(one_block['end'])

                            one_block = {"start" : total_frames/src.samplerate , "end" : -1 , "data" : [pitch]}
                        else:
                            one_block['data'].append(pitch)
                            one_block['end'] = total_frames/src.samplerate

                    #print(f"[{total_frames/src.samplerate:.2f}s] {current_pitch.nameWithOctave} [{pitch:.2f} Hz]")

            total_frames += read      # 현재 위치 업데이트
            if read < src.hop_size:   # 버퍼 사이즈보다 재생한게 작으면 종료 = 더 이상 재생할 거 없는 경우 의미
                if one_block['end'] - one_block['start'] > threshold_time:
                                filtered_pitches['value'].append(max(one_block['data']))
                                filtered_pitches['time'].append(one_block['end'])
                break

        # 최고 음역대 출력
        #print(f"Highest pitch in the range [80Hz, 830Hz]: {max_pitch_note} [{max_pitch:.2f} Hz]")

        #print(filtered_pitches)
        print("MAX PITCH : ", max(filtered_pitches['value']))
        return(max(filtered_pitches['value']))
    
    def remove_extraHz(self, file_path,output_path,highest_pitch):
        # wav 파일 로드
        y, sr = librosa.load(file_path)

        # STFT 수행
        D = librosa.stft(y)

        # 주파수 범위 설정 (80Hz ~ highestHz)
        low_bound = 80
        high_bound = 830

        # 주파수 bin 범위 계산
        freqs = librosa.fft_frequencies(sr=sr, n_fft=2048)
        low_bin = np.abs(freqs - low_bound).argmin()
        high_bin = np.abs(freqs - high_bound).argmin()

        # 범위 외의 주파수 성분 제거
        D[:low_bin, :] = 0
        D[high_bin:, :] = 0

        # 변형된 주파수 도메인 데이터를 시간 도메인으로 변환
        y_out = librosa.istft(D)
        # 음량 정규화
        y_out_normalized = librosa.util.normalize(y_out)
        # 데이터 타입 변환 (부동 소수점 -> 16비트 정수)
        y_out_int = (y_out_normalized * 32767).astype(np.int16)
        # 저장
        wavfile.write(output_path, sr, y_out_int)

    def extract_user_frequency(self,file_path, time_interval=0.1, volume_thresh=0.01): # time_interval[출력 시간 간격], volume_thresh[볼륨 임계값]

        pDetection = aubio.pitch("default", 2048, 2048//2, 44100)       # aubio.pitch : 주파수(음높이)감지 객체 생성 코드
                                                                        # ("default", 2048, 2048//2, 44100)은 각각 감지 알고리즘, 버퍼 사이즈, 버퍼 간격 사이즈, 샘플링 레이트인데 기본값 사용해서 안중요함
        pDetection.set_unit("Hz")                                       # set_unit : 주파수 감지 객체의 단위를 Hz로 정한 거임
        pDetection.set_silence(-20)                                     # 소리 감지 임계값을 -40dB로 설정(** 이걸로 삐슝빠슝 없앨 수 있긴한데 노래마다 다를거같아서 못건드리고 있음. 서버 열어서 돌려보고 판단)

        src = aubio.source(file_path, 44100, 2048//2)    # 오디오 파일 가져오는 코드(liborsa의 file.readwav랑 똑같은 역할)
        total_frames = 0                                 # 현재까지 재생한 프레임 위치(시간) 축적 변수

        pitches = []            # 감지된 주파수를 저장하는 리스트
        times = []              # 주파수 감지 될때의 시간을 저장하는 리스트

        while True:
            samples, read = src()                             # samples : 버퍼 샘플 데이터 배열 / read : 버퍼 사이즈
            pitch = pDetection(samples)[0]                    # samples[0]은 감지된 주파수 / samples[1]은 감지된 주파수의 신뢰성(** 이거 threashold 잡아서 이상치 처리도 가능해 보이긴 함)
            volume = np.sum(samples**2) / len(samples) * 100  # volume 구하는건데 이건 그냥 전자기학적 공식

            if pitch and volume > volume_thresh:              # 감지된 주파수가 있고 and 그 주파수가 볼륨 임계값 보다 큰 경우,
                current_pitch = music21.pitch.Pitch()         # 현재의 주파수를 기준으로 해서 music21객체 생성
                current_pitch.frequency = pitch               # Hz를 음계로 변환하기 위해서 현재 주파수를 music21 객체의 picth로 설정

                ##########################  ##########################
                if 80 <= pitch <= 830:
                    pitches.append(pitch)
                    times.append(total_frames/src.samplerate)
            total_frames += read
            if read < src.hop_size:
                break
        return dict(zip(times,pitches))

    def estimate_c(self,user_voice_path, app_pitch):

        # 사용자의 음성데이터로부터 시간별 주파수를 가져오는 부분
        user_frequency_dict = self.extract_user_frequency(user_voice_path)
        user_data_number = len(user_frequency_dict)
        correct_rate_list = dict()

        for scale, range in self.note_freq_dict.items():
            correct = 0
            for sec, freq in user_frequency_dict.items():
                if self.note_freq_dict[scale][0] <= freq <= self.note_freq_dict[scale][1]:
                    correct += 1
            correct_rate_list[scale] = (correct/user_data_number)
        return int(correct_rate_list[app_pitch]*100)

    def sendS3(self,file_name, key, bucket = "musicvocal"):
        '''
            file_name = '업로드 할 filepath' 
            bucket = '업로드될 버킷 이름'
            key = 's3 내에 저장될 파일명' # 폴더 안에 넣고 싶다면 폴더/파일명으로 입력
        '''
        self.s3.upload_file(file_name, bucket, key)

    def downloadS3(self,file_name, key, bucket = "musicvocal"):
        '''
            file_name = '로컬에_저장할_파일_이름'
            bucket = '업로드될 버킷 이름'
            key = '다운로드할_파일의_키를_입력하세요'
        '''
        self.s3.download_file(bucket, key, file_name)      

    def get_apikey(self,key_name = "s3" ,csv_filename='secret.csv'):
        if(not os.path.isfile(csv_filename)):
            print("CSV File Not Found")
            raise FileNotFoundError
        try :
            df = pd.read_csv(csv_filename)
            access_key = df['accesskey'][0]
            secret_key = df['secretkey'][0]
            return access_key,secret_key
        except Exception as e:
            print(f"{e} is occurend in {key_name}_get_apikey")
            return False
        
    def remove_special_characters(self,text):

        # 정규표현식을 사용하여 괄호 안의 내용을 제거
        pattern = r"\([^)]*\)"

        cleaned_text = re.sub(pattern, "", text)

        # 패턴들을 하나의 정규 표현식으로 합치기
        pattern_strings = ["Official", "Lyric", "Video", "Music", "feat", "Official Audio", "\]", "\[", "\)", "\(", "\/'", "/"]
        pattern = r'|'.join(map(re.escape, pattern_strings))
        cleaned_text = re.sub(pattern, '', text)
        
        pattern = r'[^a-zA-Z0-9가-힣\s]'  # 영어 대소문자, 한글, 공백만 남기고 삭제
        cleaned_text = re.sub(pattern, '', cleaned_text)

        for i in range(6,1,-1):
            cleaned_text = cleaned_text.replace(" "*i," ")
        cleaned_text = cleaned_text.replace(" ","_")
        return cleaned_text

    def extract_youtube(self,playlist_csv,data):
        for i in range(len(data)):
            print(f"{i}th music is trying..")
            # link 데이터 추출
            song = data.loc[i]
            music_title = song['title']
            url = song['link']
            playlist_title = song['playlist_title']
            music_path = f"./music/{playlist_title}/{music_title}.mp3"
            spleeter_path = f"./spleeter/{playlist_title}/{music_title}.wav"
            highest_pitch = -1
            
            if song['duration'] > 420 or song['duration']< 90:
                print("It's too long music")
                continue
            try:
                # Download
                if not song['youtube_downloaded'] or song['youtube_downloaded'] == "False"                                                               or '[Errno 2]' in song['youtube_downloaded']:# or "System error" in song['youtube_downloaded']:
                    self.download_youtube_video_as_mp3(url,music_title,playlist_title)
                    data.loc[i, 'youtube_downloaded'] = True
                    time.sleep(0.3)
                # spleeter
                if not song['spleeter'] or song['spleeter'] == "False":
                    self.calSpleeter(spleeter_path,music_path)
                    data.loc[i, 'spleeter'] = True
                    time.sleep(0.3)
                if not song['vocal_range'] or song['vocal_range'] == "False":
                    highest_pitch = self.extract_highest_pitch(spleeter_path)
                    data.loc[i, 'vocal_range'] = str(highest_pitch)
                    data.to_csv(playlist_csv,index = False)
                    print("vocal_range_extract completed")
                # os.remove(music_path)
                # self.sendS3(spleeter_path,spleeter_path)
                # os.remove(spleeter_path)
                print(f"{i}th music is completed")
                return highest_pitch
            except Exception as e:
                return e
                # data.loc[i] = [music_title,url,song['id'],playlist_title,song['duration'],song['uploader'],e,False,False,False,False,False]
                # data.to_csv(playlist_csv,index = False)
                # return e

    def run(self,playlist_csv,data):
        # 폴더 생성
        os.makedirs("./music", exist_ok=True)
        os.makedirs("./spleeter", exist_ok=True)
        
        for i in range(len(data)):
            print(f"{i}th music is trying..")
            # link 데이터 추출
            song = data.loc[i]
            music_title = song['title']
            url = song['link']
            playlist_title = song['playlist_title']
            music_path = f"./music/{playlist_title}/{music_title}.mp3"
            spleeter_path = f"./spleeter/{playlist_title}/{music_title}.wav"
            
            if song['duration'] >= 600:
                print("It's too long music")
                continue
            try:
                # Download
                if not song['youtube_downloaded'] or song['youtube_downloaded'] == "False" or '[Errno 2]' in song['youtube_downloaded'] :
                    self.download_youtube_video_as_mp3(url,music_title,playlist_title)
                    data.loc[i, 'youtube_downloaded'] = True
                    time.sleep(0.3)
                # original mfcc
                if not song['mfcc_origin'] or song['mfcc_origin'] == "False":
                    mfcc_val = self.extract_mfcc_features(music_path)
                    data.loc[i, 'mfcc_origin'] = str(mfcc_val)
                # spleeter
                if not song['spleeter'] or song['spleeter'] == "False":
                    self.calSpleeter(spleeter_path,music_path)
                    data.loc[i, 'spleeter'] = True
                    time.sleep(0.3)
                    data.to_csv(playlist_csv,index = False)
                
                try:
                    os.remove(music_path)
                    highest_pitch = self.extract_highest_pitch(spleeter_path)
                    print(f"{i}th music is completed")
                    return highest_pitch
                except OSError as e:
                    print(f"삭제 중 오류가 발생했습니다:", e)
                    return {"최고음 추출 실패"}
            except Exception as e:
                print(e)
                data.loc[i] = [music_title,url,song['id'],playlist_title,song['duration'],song['uploader'],e,False,False,False,False,False]
                data.to_csv(playlist_csv,index = False)

    def activate_extrawav(self,playlist_csv,data):
        # 폴더 생성
        os.makedirs("./removed_space", exist_ok=True)
        os.makedirs("./removed_extra", exist_ok=True)
        os.makedirs("./removed_space", exist_ok=True)
        os.makedirs(f"./removed_space/{playlist_csv[:-4]}", exist_ok=True)

        for i in range(len(data)):
            try:
                print(f"{i}th music is trying..")
                song = data.loc[i]
                url = song['link']
                music_title = song['title']
                playlist_title = song['playlist_title']
                # 폴더 생성 확인
                os.makedirs(f"./removed_space/{playlist_title}", exist_ok=True)
                os.makedirs(f"./removed_extra/{playlist_title}", exist_ok=True)

                spleeter_path = f"./spleeter/{playlist_title}/{music_title}.wav"    
                remove_silence_path = f"./removed_space/{playlist_title}/{music_title}.wav"
                output_filepath = f"./removed_extra/{playlist_title}/{music_title}.wav"

                # remove_silence
                if not song['remove_space_wav_to_s3'] or song['remove_space_wav_to_s3'] == "False":
                    self.remove_silence(spleeter_path,remove_silence_path)
                    data.loc[i, 'remove_space_wav_to_s3'] = True

                # 최고음 추출
                if not song['vocal_range'] or song['vocal_range'] =="False":
                    highest_pitch = self.extract_highest_pitch(remove_silence_path)
                    data.loc[i, 'vocal_range'] = highest_pitch
                    time.sleep(0.1)
                    
                    #  extraHz 제외
                    self.remove_extraHz(remove_silence_path,output_filepath,highest_pitch)

                    # S3 저장 후 파일 삭제
                    self.sendS3(output_filepath,output_filepath)
                
                
                # mfcc 계산
                if not song['mfcc_after_transform'] or song['mfcc_after_transform'] =="False":
                    mfcc_value = self.extract_mfcc_features(output_filepath)
                    data.loc[i, 'mfcc_after_transform'] = str(mfcc_value)
                    data.to_csv(playlist_csv,index = False)

                try:
                    os.remove(spleeter_path)
                    os.remove(remove_silence_path)
                    os.remove(output_filepath)
                except OSError as e:
                    print(f"삭제 중 오류가 발생했습니다:", e)
                print(f"{i}th music is completed")

            except Exception as e:
                print(e)
                data.loc[i] = [music_title,url,song['id'],playlist_title,song['duration'],song['uploader'],e,False,False,False,False,False]
                data.to_csv(playlist_csv,index = False)