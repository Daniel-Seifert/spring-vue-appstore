package base.appstore.controller;

import base.appstore.controller.dto.AppDto;
import base.appstore.controller.dto.CommentDto;
import base.appstore.controller.dto.RatingDto;
import base.appstore.exceptions.ResourceNotFoundException;
import base.appstore.model.App;
import base.appstore.model.Comment;
import base.appstore.model.Rating;
import base.appstore.model.User;
import base.appstore.repository.AppRepository;
import base.appstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;


@RestController
@RequestMapping("/api/apps")
@CrossOrigin
public class AppController {


    @Autowired
    private AppRepository appRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public Stream<AppDto> getAllAppsFiltered(@RequestParam(required = false) String search
            , @RequestParam(required = false) String tag
            , @RequestParam(required = false) Long rating) {
        return appRepository.findAll()
                .stream()
                .map(AppDto::new);
    }



    @GetMapping("{id}")
    public AppDto getApp(@PathVariable Long id) {
        final App app = getAppByIdOrThrow(id);

        // Increment views
        app.setViews(app.getViews() + 1);
        appRepository.save(app);

        return new AppDto(app);
    }

    @PostMapping("{id}/ratings")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER', 'DEVELOPER', 'ADMIN')")
    public double createRating(@PathVariable Long id, @RequestBody RatingDto ratingDto) {
        final App app = getAppByIdOrThrow(id);
        final User author = userRepository.findById(ratingDto.getAuthor().getId()).orElseThrow(ResourceNotFoundException::new);
        final Rating rating = ratingDto.toEntity();

        rating.setAuthor(author);
        app.addRating(rating);

        appRepository.save(app);
        return app.getRatings().stream().mapToDouble(Rating::getStars).average().orElse(0);
    }

    @PostMapping("{id}/comments")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER', 'DEVELOPER', 'ADMIN')")
    public CommentDto createComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        final App app = getAppByIdOrThrow(id);
        final User author = userRepository.findById(commentDto.getAuthor().getId()).orElseThrow(ResourceNotFoundException::new);
        final Comment comment = commentDto.toEntity();

        comment.setAuthor(author);
        app.addComment(comment);
        appRepository.save(app);

        return new CommentDto(comment);
    }

    @GetMapping("{id}/ratings")
    public Stream<RatingDto> getRatings(@PathVariable Long id) {
        final App app = getAppByIdOrThrow(id);
        return app.getRatings().stream().map(RatingDto::new);
    }

    @GetMapping("{id}/comments")
    public Stream<CommentDto> getComments(@PathVariable Long id) {
        final App app = getAppByIdOrThrow(id);
        return app.getComments().stream().map(CommentDto::new);
    }

    private App getAppByIdOrThrow(Long id) {
        return appRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

}
