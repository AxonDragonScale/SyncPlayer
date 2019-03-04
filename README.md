# SyncPlayer

An android application for synchronized video and audio playback on multiple devices.



# Project Proposal : Synchronized audio and video on multiple devices.

### Team 

- **Ronak Harkhani &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2016A7PS0078G &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;f20160078@goa.bits-pilani.ac.in**
- **Anish Bhobe &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2016A7PS0030G &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;f20160030@goa.bits-pilani.ac.in**

### Motivation

As of 2019, Smartphones are ubiquitous and have taken the place of conventional telephones as well as media devices. For example, [Spotify](https://www.spotify.com), a digital music streaming platform has about 89 million unique users. Similarly cable networks such as HBO are being overtaken by Video on Demand (VoD) streaming services such as [Netflix](https://www.netflix.com).

In this age of smartphones, television still reigns as the most important media device since sharing a smartphone screen is not comfortable. However televisions are expensive and non-portable. Similarly, headphones or external speakers are the primary audio output for most phones due to them offering better audio quality as compared to the in-built speakers, however sharing them is inconvenient.

Hence, we need a utility or service that can allow shared media consumption across multiple personal devices.

### Objectives

- #### Problem Statement

  To deliver an application that allows convenient syncronized shared listening/viewing of audio/video on supported devices. 

- #### Deliverables

  An application that can:

  1. Seamlessly allow connections between multiple devices.
  2. Stream audio/video from a host to a client smartphones.
  3. Provide audio playback synchronization.
  4. Provide video playback synchronization.

  (Additionally) A web application that can allow viewing the stream from a web browser.

- #### Future Scope

  The app can be extended to provide virtual rooms such that the app can run over the internet instead of being constrained to (W)LAN. However this is out of scope for the project due to time constraints.

### Related Work

- [AmpMe](https://www.ampme.com) - Mobile application for music synchronization across mobile phones to make it louder. AmpMe works with music streaming services like spotify, soundcloud, etc. AmpMe achieves  a part of what this projects aims to do but it doesn't support synchronized video streaming and file sharing.
- [SyncPlay](https://syncplay.pl) - Computer application for video synchronization across conventional computers. SyncPlay is not available for mobile phones and other portable devices where it's needed more.

### Prerequisites

- Android Devices supporting **WiFi Direct** feature.
- Wireless Local Area Network or Hotspot.
- Appropriate Audio/Video codecs.

### Milestones

1. An application that can connect to other devices and transfer audio/video files.
2. Add support to play audio/video files.
3. Synchronize the audio/video playback.
4. Setup an HTTP server in the application to stream the media instead of sending files beforehand.
5. (Optional) Add support for streaming through web browsers.

### Challenges and Research Needed

- Implementing streaming from an android device based server.
- [Synchronization with accounting for latency and transfer delays.](https://pub.tik.ee.ethz.ch/students/2015-FS/SA-2015-02.pdf)
