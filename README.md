# MusikMorpher API

This is a REST API that allows you to speed up and slow down songs. It uses
ffmpeg in the backend to process the audio files.

## Endpoints

### POST /upload

This endpoint allows you to upload an audio file and specify whether it should
be slowed down.

#### Parameters

- `file`: The audio file to be uploaded.
- `slowed` (optional): A string indicating whether the audio file should be
  slowed down.

#### Responses

- 200 OK: Returns the URL to download the processed audio file.
- 400 Bad Request: If the uploaded file is not an audio file.
- 500 Internal Server Error: If there was an error during ffmpeg process.

### GET /download/{fileCode}

This endpoint allows you to download a previously uploaded file.

#### Parameters

- `fileCode`: The code of the file to be downloaded.

#### Responses

- 200 OK: The requested file.
- 404 Not Found: If the file with the provided code does not exist.
- 500 Internal Server Error: If there was an error retrieving the file.

## Setup

- `git clone https://github.com/caiohenrique-3/musik-morpher-api.git`
- `mvn exec:java`

The API will run on `http://127.0.0.1:9090`

## Usage

- `curl -X POST -F "file=@test.mp3" -F "slowed=true" http://localhost:9090/upload`

```json
{
  "fileName": "test.mp3",
  "fileCode": "/download/onpmo16N",
  "size": 1.72
}
```

- `curl http://localhost:9090/download/onpmo16N --output testOutput.mp3`
