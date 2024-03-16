import aubio                      # 주파수 탐지 라이브러리
import numpy as np
import music21                    # 주파수 -> 음계 변환 라이브러리
import matplotlib.pyplot as plt   # 그래프 작성 라이브러리
from scipy.signal import medfilt  # 중간값 필터 라이브러리 -> 이상치를 중앙값으로 대체하기 위한 라이브러리 

def extract_highest_pitch(file_path, time_interval=0.1, volume_thresh=0.01): # time_interval[출력 시간 간격], volume_thresh[볼륨 임계값]

    pDetection = aubio.pitch("default", 2048, 2048//2, 44100)       # aubio.pitch : 주파수(음높이)감지 객체 생성 코드
                                                                    # ("default", 2048, 2048//2, 44100)은 각각 감지 알고리즘, 버퍼 사이즈, 버퍼 간격 사이즈, 샘플링 레이트인데 기본값 사용해서 안중요함
    pDetection.set_unit("Hz")                                      # set_unit :  주파수 감지 객체의 단위를 Hz로 정한 거임
    pDetection.set_silence(-40)                                     # 소리 감지 임계값을 -40dB로 설정(** 이걸로 삐슝빠슝 없앨 수 있긴한데 노래마다 다를거같아서 못건드리고 있음. 서버 열어서 돌려보고 판단)

    src = aubio.source(file_path, 44100, 2048//2)    # 오디오 파일 가져오는 코드(liborsa의 file.readwav랑 똑같은 역할)
    total_frames = 0                                 # 현재까지 재생한 프레임 위치(시간) 축적 변수 

    pitches = []            # 감지된 주파수를 저장하는 리스트
    times = []              # 주파수 감지 될때의 시간을 저장하는 리스트
    max_pitch = 0           # 최고 음역대 저장 변수
    max_pitch_note = None   # 최고 음역대(Hz)에 대응되는 음계 저장 변수 - ex) max_pitch가 400Hz일때 A#4 이런식으로 저장되게 하려고

    while True:
        samples, read = src()                             # samples : 버퍼 샘플 데이터 배열 / read : 버퍼 사이즈
        pitch = pDetection(samples)[0]                    # samples[0]은 감지된 주파수 / samples[1]은 감지된 주파수의 신뢰성(** 이거 threashold 잡아서 이상치 처리도 가능해 보이긴 함)
        volume = np.sum(samples**2) / len(samples) * 100  # volume 구하는건데 이건 그냥 전자기학적 공식

        if pitch and volume > volume_thresh:              # 감지된 주파수가 있고 and 그 주파수가 볼륨 임계값 보다 큰 경우, 
            current_pitch = music21.pitch.Pitch()         # 현재의 주파수를 기준으로 해서 music21객체 생성
            current_pitch.frequency = pitch               # Hz를 음계로 변환하기 위해서 현재 주파수를 music21 객체의 picth로 설정


            ########################## 사람 음성 최저 음역대와 최고 음역대로 범위 설정 ##########################

            if 80 <= pitch <= 830:
                pitches.append(pitch)                             # 감지된 주파수를 주파수 리스트에 저장
                times.append(total_frames/src.samplerate)         # 감지 시간을 시간 리스트에 저장 
                if pitch > max_pitch:                             # 최고 음계 뽑는 코드(*근데 이거 이상치 제거 안한 상태에서 뽑는거라 수정해야함) 
                                                                  # (**ㄴㄴ 이상치 제거한 상태에서 하면 값 이상해짐.. 방법 찾아야함)
                    max_pitch = pitch
                    max_pitch_note = current_pitch.nameWithOctave
                print(f"[{total_frames/src.samplerate:.2f}s] {current_pitch.nameWithOctave} [{pitch:.2f} Hz]")

            #####################################################################################################


        total_frames += read      # 현재 위치 업데이트
        if read < src.hop_size:   # 버퍼 사이즈보다 재생한게 작으면 종료 = 더 이상 재생할 거 없는 경우 의미
            break



    ################### 윈도우 크기 설정 후 이상치를 중앙값으로 대체 #######################

    # 만약 커널 사이즈가 11이면 이전 데이터 5개와 이후 데이터 5개를 나열시킴
    # 주어진 데이터가 [1, 1, 2, 2, 4, 2, 1, 2, 1, 1 ,1]이고 현재 위치가 4라고 했다면 [1, 1, 1, 1, 1, 1, 2, 2, 2, 4]가 되면서 현재값(4)가 중앙값(1)로 변경됨

    for pitch in pitches:
        
    # kernel_size 계산: -0.3s ~ 0.3s 범위 내의 데이터 포인트 수
    kernel_size = 61 # 음성 데이터 시간 별로 쭉 뽑아보니까 0.3초 안에 변하는것들은 그냥 노이즈였음 그래서 기준은 0.3으로 잡은겨

    # kernel_size가 홀수가 되도록 조정
    if kernel_size % 2 == 0:
        kernel_size += 1

    # 중간값 필터링을 통해 이상치 제거
    filtered_pitches = medfilt(pitches, kernel_size=kernel_size)

    #######################################################################################


    # 최고 음역대 출력
    print(f"Highest pitch in the range [80Hz, 830Hz]: {max_pitch_note} [{max_pitch:.2f} Hz]")  

    # 그래프 그리기 
    plt.figure(figsize=(12, 6))
    plt.plot(times, filtered_pitches, marker='o', linestyle='-')
    plt.xlabel('Time (s)')
    plt.ylabel('Frequency (Hz)')
    plt.title('Pitch Detection')
    plt.grid(True)
    plt.tight_layout()
    plt.show()





extract_highest_pitch('/content/gdrive/MyDrive/MusicData/voice_data/tears.wav', volume_thresh=0.001)