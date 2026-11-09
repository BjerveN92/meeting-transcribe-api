# meeting-transcribe-api




## Key Features and Functionalities
 
- **Audio Transcription** - Upload recorded audio files or capture meetings directly. 

- **Automatic Transcoding** - Files that are 20 MB and above are automatically compressed via ffmpeg into 16 KhZ and if the audio is in stereo it goes into mono. 
  This is enough for preserving speech quality while reducing upload size and latency.
  
- **AI-Powered Meeting Analysis** - LLM/SLM generates corrected full text, bullet-point summary, action items, decisions, and participant list.

- **Meeting Profiles** - A sprint planning needs precision, a brainstorm needs creative interpretation. Each profile tailors the AI’s prompt and behavior to match the meeting type.

- **RAG Context Enrichment** - The user should be able to upload manual context that are relevant to the actuall meeting, for a better result. In RAG we trust! 

- **Unified API Gateway** - Single OpenRouter integration for both STT and LLM/SLM models - switch models by changing some config values. Easy-peasy-lemon-squeezy! 

- **Future: Translation** - Translate meeting summaries to other languages. 
