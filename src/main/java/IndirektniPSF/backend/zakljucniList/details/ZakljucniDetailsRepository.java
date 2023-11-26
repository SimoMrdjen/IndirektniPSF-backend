package IndirektniPSF.backend.zakljucniList.details;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZakljucniDetailsRepository extends JpaRepository<ZakljucniListDetails, Integer>
{
//    List<ZakljucniListDetails> getAllByGEN_MYSQL(Integer zakljucniList);
}
