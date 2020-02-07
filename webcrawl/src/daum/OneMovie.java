package daum;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OneMovie {
	public static void main(String[] args) throws IOException {
		// 1페이지의 평점 10건 수집
		String url = "https://movie.daum.net/moviedb/grade?movieId=134091";
//		int page = 1;
//		String url = base + page;
		
				
			
	
		Document doc = Jsoup.connect(url).get();

		Elements reply = doc.select("ul.list_netizen div.review_info");

		// 영화제목 수집
		Elements movie = doc.select("h2.tit_rel");

		int score, regdate, count = 0;
		String writer, content, basedate, subdate = "";
		
		
		
		for (Element one : reply) {
			count++;
			writer = one.select("em.link_profile").text();
			score = Integer.parseInt(one.select("em.emph_grade").text());
			content = one.select("p.desc_review").text();
			basedate = one.select("span.info_append").text();
			subdate = basedate.substring(0, 10);
			regdate = Integer.parseInt(subdate.replace(".", ""));

			System.out.println("■■■" + count + "건■■■■■■■■■■■■■■■■■■■■■■■■■■■");
			System.out.println("영화:" + movie.text());
			System.out.println("평점:" + score);
			System.out.println("작성자:" + writer);
			System.out.println("내용:" + content);
			System.out.println("작성일자:" + regdate);
		}
		
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ 수집한 평점은 총"+count+ "건 입니다");
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		// 평점 
		// 작성자 
		// 내용 
		// 작성일자 
		// 수집 
		}
	
	}


