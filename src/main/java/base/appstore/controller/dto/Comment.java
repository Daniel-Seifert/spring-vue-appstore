package base.appstore.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Long id;
    private String commentText;
    private Date createDate;
    private User author;
}
