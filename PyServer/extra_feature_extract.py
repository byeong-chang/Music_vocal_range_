import librosa
import ExtractYoutube
import numpy as np
import pandas as pd
import time
import os 
import yt_dlp

if os.path.exists(f"full_data_extra.csv"):
    log_datas = pd.read_csv(f"full_data_extra.csv")
else:
    log_datas = pd.DataFrame(columns=[
        'playlist_title', 'id', 'tempo', 
        'spectral_rolloff_mean', 'spectral_rolloff_variance',
        'spectral_centroids_mean', 'spectral_centroids_variance',
        'zero_crossings_mean', 'zero_crossings_variance',
        'mfcc_mean', 'mfcc_variance',
        'harmonic_mean', 'harmonic_variance',
        'percussive_mean', 'percussive_variance',
        'chroma_mean', 'chroma_variance'
    ])
def summarize_feature(feature, axis=None):
    summary = {
        'mean': np.round(np.mean(feature, axis=axis),2),
        'variance': np.round(np.var(feature, axis=axis),2),
    }
    return summary

data = pd.read_csv('full_data_2.csv')

for i in range(len(log_datas),len(data)):
            
    try:
        print(f"{i}th music is trying..")
        # link 데이터 추출
        song = data.loc[i]
        music_title = song['title']
        mfcc_str = song['mfcc_origin']
        url = song['link']
        playlist_title = song['playlist_title']
        music_path = f"./music/{playlist_title}/{music_title}.mp3"
        id = song['id']
        mfcc_str = mfcc_str.replace("[", "").replace("]", "").strip()

        # 쉼표 또는 공백을 기준으로 분할하여 부동 소수점 수로 변환
        mfcc = np.array([float(x) for x in mfcc_str.split()])

        ydl_opts = {
            'format': 'worstaudio',
            'postprocessors': [{
                'key': 'FFmpegExtractAudio',
                'preferredcodec': 'mp3',
                'preferredquality': '192',
            }],
            'ignoreerrors': True,
            'outtmpl': f'./music/{playlist_title}/{music_title}.%(ext)s',
            'retries': 10,
            'fragment-retries': 'infinite',
            'skip-unavailable-fragments': True,
            'quiet': True  # 출력 억제
        }

        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            ydl.download([url])
            
        time.sleep(0.1)

        y, sr = librosa.load(music_path)

        # Tempo(BPM)
        tempo, _ = librosa.beat.beat_track(y=y, sr=sr)

        # Zero Crossing Rate
        zero_crossings = librosa.feature.zero_crossing_rate(y)[0]

        # Spectral Centroid
        spectral_centroids = librosa.feature.spectral_centroid(y=y, sr=sr)[0]

        # Spectral Rolloff
        spectral_rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr, roll_percent=0.85)[0]

        # Harmonic and Percussive Components
        harmonic, percussive = librosa.effects.hpss(y)

        # Chroma Frequencies
        chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)

        #요약 생성
        spectral_rolloff_summary = summarize_feature(spectral_rolloff)
        spectral_centroids_summary = summarize_feature(spectral_centroids)
        zero_crossings_summary = summarize_feature(zero_crossings)
        mfcc_summary = summarize_feature(mfcc)
        harmonic_summary = summarize_feature(harmonic)
        percussive_summary = summarize_feature(percussive)
        chroma_summary = summarize_feature(chroma_stft, axis=1)

        value = [playlist_title, id, tempo, spectral_rolloff_summary['mean'], spectral_rolloff_summary['variance'],
                spectral_centroids_summary['mean'], spectral_centroids_summary['variance'],
                zero_crossings_summary['mean'], zero_crossings_summary['variance'],
                mfcc_summary['mean'], mfcc_summary['variance'],
                harmonic_summary['mean'], harmonic_summary['variance'],
                percussive_summary['mean'], percussive_summary['variance'],
                chroma_summary['mean'], chroma_summary['variance']]
        log_datas.loc[i] = value

        log_datas.to_csv(f"full_data_extra.csv",index = False)
        os.remove(music_path)
    except Exception as e:
        print(e)
        value = [False] * 17
        log_datas.loc[i] = value
        log_datas.to_csv(f"full_data_extra.csv",index = False)