package preonboarding.board.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Paging {

    public static Pageable createPageRequest(int page, int size) {
        page = Math.max(page, 1);
        return PageRequest.of(page - 1, size);
    }
}
