package pl.rotkom.friday.thirdparty.google.youtube.api.service;

import java.util.List;

import pl.rotkom.friday.thirdparty.google.youtube.api.dto.ChannelDTO;
import pl.rotkom.friday.thirdparty.google.youtube.api.dto.VideoDTO;


public interface IYouTubeApi {

    List<String> getLatestVideoIds(List<String> channelIds);

    List<VideoDTO> getVideos(List<String> videoIds);

    List<ChannelDTO> getSubscribedChannels();
}
