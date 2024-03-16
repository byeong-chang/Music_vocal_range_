from fastapi import FastAPI
from youtube_download import youtube_download
app = FastAPI()

@app.get("/")
async def root():
    youtube_download("https://www.youtube.com/watch?v=ayREauImhZg&ab_channel=%EC%9B%85%ED%82%A4")
    return {"message": "Hello World"}