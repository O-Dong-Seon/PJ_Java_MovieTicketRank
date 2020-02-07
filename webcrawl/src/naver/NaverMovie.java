package naver;

import java.io.IOException;
import java.text.DecimalFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import domain.MovieDTO;
import persistence.MovieDAO;

public class NaverMovie {
	public static void main(String[] args) throws IOException{
		String base = "https://movie.naver.com/movie/bi/mi/pointWriteFormList.nhn?code=191431&type=after&isActualPointWriteExecute=false&isMileageSubscriptionAlready=false&isMileageSubscriptionReject=false&page=";
		int page = 1;      // 수집한 페지이수(1page=댓글10개)
		String url = base + page;
		int count = 0;     // 전체 댓글수
		String title = ""; // 영화제목
		int total = 0;     // 평점을 모두 더하는 변수
		double scoreAvg = 0.0;  // 평균 평점
		String compare = ""; // 마지막 페이지 종료를 위한 변수
		MovieDAO mDao = new MovieDAO();
		
		// 영화제목 수집
		Document movieDoc = Jsoup.connect("https://movie.naver.com/movie/bi/mi/basic.nhn?code=191431").get();
		title = movieDoc.select("h3.h_movie > a:nth-child(1)").first().text();
		
		// 페이지를 돌면서 댓글을 수집!
		label:while(true) {
			// 1페이지의 평점 10건 수집
			Document doc = Jsoup.connect(url).get();
			Elements reply = doc.select("div.score_result > ul > li");
			
			
			int score=0;
		    int regdate=0;
			String writer, content = "";
			// 평점 1건 수집
			for (int i = 0; i < reply.size(); i++) {
				score = Integer.parseInt(reply.get(i).select("div.star_score > em").text());
				content = reply.get(i).select(".score_reple > p").text();
				writer = reply.get(i).select(".score_reple dt em:first-child").text();
				regdate = Integer.parseInt(reply.get(i).select(".score_reple dt em:last-child").text().substring(0, 10).replace(".", ""));
				
				// 마지막 페이지 종료
				if(i == 0) {
			    	if(compare.equals(writer)) {
			    		break label;
			    	} else {
			    		compare = writer;
			    	}
			    }
				
				count++;
				// 누적 평점을 계산
				total += score; //total = total + score; 

						
				// DTO에 담긴 1건의 평점을 DB에 저장
				System.out.println("◑◐"+count+"건■◐◑◐◑◐◑◑◐◑◐◑◐◑◐◑◐◑◐◑◐◐◑◐◑◐◑◐◑◐◑◑◐◑◐◑◐◑◐◑◐◑◐◑◐◐◑◐◑◐◑");
				System.out.println("영화: " + title);
				System.out.println("평점: " + score);
				System.out.println("작성자: " + writer);
				System.out.println("내용: " + content);
				System.out.println("작성일자: " + regdate);
			}
			// 다음 페이지로 이동하기 위해 page+1 증가s
			page = page + 1;
			// 다음 페이지로 이동할 URL 작성
			url = base + page;
		}
		// 평균 평점 계산
		scoreAvg = (double)total / count;
		// 소수점 첫번째 자리까지 출력(버림)
		double result = scoreAvg;
		DecimalFormat df = new DecimalFormat("0.0");

		
		// 수집 및 분석결과 출력
		System.out.println("◐◑◐◑◐◑◑◐◑◐◑◐◑◐◑◐◑◐◑◐◐◑◐◑◐◑◐◑◐◑◐◑◑◐◑◐◑◐◑◐◑◐◑◐◑◐◐◑◐◑◐◑◐◑◐◑◐◑◑◐◑◐◑◐◑◐◑◐◑◐◑◐◐◑◐◑◐◑");
		System.out.println("◑◐ '"+title+"' NAVER영화 평점 수집결과");
		System.out.println("◑◐ "+(page-1)+"페이지에서");
		System.out.println("◑◐ "+count+"건의 평점을 수집완료");
		System.out.println("◑◐ 평균평점은 "+df.format(result)+"점:)");
		System.out.println("◐◑◐◑◐◑◑◐◑◐◑◐◑◐◑◐◑◐◑◐◐◑◐◑◐◑◑◐◑◐◑◑◐◑◐◑◐◑◐◑◐◑◐◑◐◐◑◐◑◐◑■");
	}
}
