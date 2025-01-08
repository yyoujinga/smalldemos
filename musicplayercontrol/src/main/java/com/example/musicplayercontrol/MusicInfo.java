package com.example.musicplayercontrol;

import java.util.ArrayList;
import java.util.List;

public class MusicInfo {
    private long id;           // 音乐ID
    private String title;      // 歌曲名
    private String artist;     // 艺术家
    private String album;      // 专辑名
    private String duration;   // 时长
    private String path;       // 文件路径
    private String albumArtUri; // 专辑封面URI
    private long albumId;      // 专辑ID
    private long artistId;     // 艺术家ID
    private int trackNumber;   // 音轨号

    // 构造函数
    public MusicInfo(long id, String title, String artist, String album, String duration,
                     String path, String albumArtUri, long albumId, long artistId, int trackNumber) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.path = path;
        this.albumArtUri = albumArtUri;
        this.albumId = albumId;
        this.artistId = artistId;
        this.trackNumber = trackNumber;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    // 用于测试的示例数据生成器
    public static List<MusicInfo> getTestData() {
        List<MusicInfo> musicList = new ArrayList<>();

        musicList.add(new MusicInfo(
                1,
                "Bohemian Rhapsody",
                "Queen",
                "A Night at the Opera",
                "5:55",
                "/storage/music/bohemian_rhapsody.mp3",
                "content://media/external/audio/albumart/1",
                1,
                1,
                1
        ));

        musicList.add(new MusicInfo(
                2,
                "Shape of You",
                "Ed Sheeran",
                "÷ (Divide)",
                "3:53",
                "/storage/music/shape_of_you.mp3",
                "content://media/external/audio/albumart/2",
                2,
                2,
                1
        ));

        musicList.add(new MusicInfo(
                3,
                "Rolling in the Deep",
                "Adele",
                "21",
                "3:48",
                "/storage/music/rolling_in_the_deep.mp3",
                "content://media/external/audio/albumart/3",
                3,
                3,
                1
        ));

        musicList.add(new MusicInfo(
                4,
                "Uptown Funk",
                "Mark Ronson ft. Bruno Mars",
                "Uptown Special",
                "4:30",
                "/storage/music/uptown_funk.mp3",
                "content://media/external/audio/albumart/4",
                4,
                4,
                1
        ));

        musicList.add(new MusicInfo(
                5,
                "Billie Jean",
                "Michael Jackson",
                "Thriller",
                "4:54",
                "/storage/music/billie_jean.mp3",
                "content://media/external/audio/albumart/5",
                5,
                5,
                1
        ));

        return musicList;
    }

    // 使用示例
    public static void main(String[] args) {
        List<MusicInfo> testData = MusicInfo.getTestData();
        for (MusicInfo music : testData) {
            System.out.println("Title: " + music.getTitle() +
                    ", Artist: " + music.getArtist());
        }
    }
}