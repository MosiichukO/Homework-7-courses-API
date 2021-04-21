package entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class ResponseBlock {
    long code;
    String type;
    String message;
}
