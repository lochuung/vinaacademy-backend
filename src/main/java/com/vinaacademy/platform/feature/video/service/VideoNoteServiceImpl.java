package com.vinaacademy.platform.feature.video.service;

import com.vinaacademy.platform.feature.common.exception.ResourceNotFoundException;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.video.dto.VideoNoteDto;
import com.vinaacademy.platform.feature.video.dto.VideoNoteRequestDto;
import com.vinaacademy.platform.feature.video.entity.Video;
import com.vinaacademy.platform.feature.video.entity.VideoNote;
import com.vinaacademy.platform.feature.video.mapper.VideoNoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.vinaacademy.platform.feature.video.repository.VideoNoteRepository;
import com.vinaacademy.platform.feature.video.repository.VideoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoNoteServiceImpl implements VideoNoteService {

    private final VideoNoteRepository videoNoteRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final VideoNoteMapper videoNoteMapper;


    @Override
    @Transactional
    public VideoNoteDto createVideoNote(UUID userId, VideoNoteRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        Video video = videoRepository.findById(requestDto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy video với ID: " + requestDto.getVideoId()));

        // Kiểm tra người dùng có quyền truy cập video này không
        // Đây là nơi bạn có thể kiểm tra xem người dùng đã đăng ký khóa học chứa video này chưa

        VideoNote videoNote = videoNoteMapper.toEntity(requestDto, user, video);
        VideoNote savedNote = videoNoteRepository.save(videoNote);

        return videoNoteMapper.toDto(savedNote);
    }

    @Override
    @Transactional
    public VideoNoteDto updateVideoNote(UUID userId, Long noteId, VideoNoteRequestDto requestDto) {
        VideoNote videoNote = videoNoteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ghi chú với ID: " + noteId));

        // Nếu người dùng thay đổi videoId, chúng ta cần kiểm tra và cập nhật video
        if (!videoNote.getVideo().getId().equals(requestDto.getVideoId())) {
            Video newVideo = videoRepository.findById(requestDto.getVideoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy video với ID: " + requestDto.getVideoId()));

            // Kiểm tra người dùng có quyền truy cập video này không

            videoNote.setVideo(newVideo);
        }

        videoNoteMapper.updateEntityFromDto(requestDto, videoNote);
        VideoNote updatedNote = videoNoteRepository.save(videoNote);

        return videoNoteMapper.toDto(updatedNote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoNoteDto> getVideoNotesByVideoAndUser(UUID userId, UUID videoId) {
        // Kiểm tra người dùng có quyền truy cập video này không

        List<VideoNote> videoNotes = videoNoteRepository.findByVideoIdAndUserId(videoId, userId);
        return videoNotes.stream()
                .map(videoNoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoNoteDto> getAllVideoNotesByUser(UUID userId) {
        List<VideoNote> videoNotes = videoNoteRepository.findByUserId(userId);
        return videoNotes.stream()
                .map(videoNoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VideoNoteDto getVideoNoteById(UUID userId, Long noteId) {
        VideoNote videoNote = videoNoteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ghi chú với ID: " + noteId));

        return videoNoteMapper.toDto(videoNote);
    }

    @Override
    @Transactional
    public void deleteVideoNote(UUID userId, Long noteId) {
        VideoNote videoNote = videoNoteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ghi chú với ID: " + noteId));

        videoNoteRepository.delete(videoNote);
    }
}
