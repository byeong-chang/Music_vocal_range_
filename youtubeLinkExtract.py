from googleapiclient.discovery import build
import pandas as pd
# API 정보 설정
api_key = 'AIzaSyAfAUUJ5xog0BHpGy1Zu3bBzotbSWAQrBE'
youtube = build('youtube', 'v3', developerKey=api_key)
extractedLink = []
video_title = []
video_channel = []
video_url = []

playlistLink = '''https://www.youtube.com/watch?v=js1CtxSY38I&list=RDCLAK5uy_kjKtb_RC7LRbxiEmSIzZqJRVcYm8U9KMc&index=7
https://www.youtube.com/watch?v=1CTced9CMMk&list=RD1CTced9CMMk'''

# 입력한 링크에서 필요한 ID만 추출
# ex) list= 뒤의 값 추출해야함. https://www.youtube.com/watch?v=1CTced9CMMk&list=RD1CTced9CMMk
for link in playlistLink.split('\n'):
    solid = link.split("list=")
    valid = solid[1].split("&")
    extractedLink.append(valid[0])
for playlist_id in extractedLink:

    playlist_items = []

    # 첫 페이지 토큰 설정
    next_page_token = None
    first_id = ''
    flag = True

    while flag:
        playlist_items_request = youtube.playlistItems().list(
            part='snippet',
            playlistId=playlist_id,
            maxResults=50,
            pageToken=next_page_token
        )
        playlist_items_response = playlist_items_request.execute()

        # 현재 페이지의 아이템 추가
        playlist_items.extend(playlist_items_response['items'])

        # 다음 페이지 토큰 업데이트
        next_page_token = playlist_items_response.get('nextPageToken')
        print("length : " , len(playlist_items))
        for item in playlist_items:
            video_id = item['snippet']['resourceId']['videoId']
            if first_id == video_id:
                flag = False
                break
            elif first_id == "":
                first_id = video_id
            # url 겹치는애 확인 필요
            url = f'https://www.youtube.com/watch?v={video_id}'
            if url in video_url:
                continue
            video_title.append(item['snippet']['title'])
            video_channel.append(item['snippet']['videoOwnerChannelTitle'])
            video_url.append(url)
data = {
    "video_title" : video_title,
    "video_channel" : video_channel,
    "video_url" : video_url
}
# DataFrame 생성
df = pd.DataFrame(data)
# DataFrame을 CSV 파일로 저장
# csv도 덮어씌우기 해야함
df.to_csv(f'youtubeLink.csv', index=False)


'''
이름에서 제거할 문자 생각해볼 예시
[Official Video]
(1, 2, 3!)
M/V
'
"
,
OFFICIAL
'''