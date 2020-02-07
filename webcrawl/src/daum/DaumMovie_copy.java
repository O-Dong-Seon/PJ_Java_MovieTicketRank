package daum;

import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.MovieDTO;
import persistence.MovieDAO;

public class DaumMovie_copy {
	public static void main(String[] args) throws IOException {
		String base = "https://movie.daum.net/moviedb/grade?movieId=134091&page=";
		int page = 1;
		String url = base + page;
		int count = 0;
		String title=""; // 영화제목
		int total = 0;   // 평점을 모두 더하는 변수
		double scoreAvg = 0.0; // 평균 평점
		
		MovieDAO mDao = new MovieDAO();
		
		// 페이지를 돌면서 댓글을 수집!
		while (true) {
			// 1페이지의 평점 10건 수집
			Document doc = Jsoup.connect(url).get();
			Elements reply = doc.select("ul.list_netizen div.review_info");

			// 영화제목 수집
			Elements movieName = doc.select("h2.tit_rel");
			title = movieName.text();
			
			if(reply.isEmpty()) {
				break;
			}

			int score, regdate = 0;
			String writer, content, basedate, subdate = "";

			for (Element one : reply) {
				count++;
				writer = one.select("em.link_profile").text();
				score = Integer.parseInt(one.select("em.emph_grade").text());
				content = one.select("p.desc_review").text();

				basedate = one.select("span.info_append").text();
				subdate = basedate.substring(0, 10); // 2020.20.06 0~10
				regdate = Integer.parseInt(subdate.replace(".", "")); // 20200206 .을 치환해줌

				//누적 평점을 계산 
				total = total + score; // total = total + score;
				
				MovieDTO mDto = new MovieDTO(title,content,writer,score,"daum",regdate);
				
				//DB에 저장
				mDao.addMovie(mDto);
				System.out.println("■■■" + count + "건■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				System.out.println("영화:" + title);
				System.out.println("평점:" + score);
				System.out.println("작성자:" + writer); 
				System.out.println("내용:" + content);
				System.out.println("작성일자:" + regdate);
			}
			// 다음 페이지로 이동하기 위해 page+1 증가
			page = page + 1;
			// 다음 페이지로 이동할 url 작성
			url = base + page;
		}
		// 평균 평점 계산
		scoreAvg = (double)total / count;
		
		double result = scoreAvg;
		DecimalFormat df = new DecimalFormat("0.0");

		
		//수집 및 분석결과 출력
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■■" + title +"Daum 영화 평점 수집 결과");
		System.out.println("■■" + (page-1) +"페이지에서");
		System.out.println("■■" + count +"건의 평점 수집 완료");
		System.out.println("■ 수집한 평균 평점은" + df.format(result) + "점 입니다");
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		// 평점
		// 작성자
		// 내용
		// 작성일자
		// 수집
	}

}
