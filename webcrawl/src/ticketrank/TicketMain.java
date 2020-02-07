/*
 * Program: Movie Crawler ver1.2
 * Developer: 
 * Date: 2020.02.07
 * Summary: 영화 실시간 예매 1~10위(네이버ㅡ다음)까지를 대상으로
 * 			네이버와 다음에서 각각 댓글(평점)을 수집하고
 * 			댓글 수와 평점 평균 통계량을 출력하는 프로그램
 * Tools: Java, Jsoup, JdbcMybatis, Oracle, SqlDeveloper, SQL
 */
package ticketrank;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TicketMain {
	public static void main(String[] args) throws IOException {

		Scanner sc = new Scanner(System.in);
		int selCode = 0; // 사용자가 선택한 영화(순위)

		// 1~10위까지 영화제목과 고유코드를 저장하는 배열
		// [i] <= 영화 1~10위
		// 배열 인덱스:0~9, 순위 인덱스: 1~10, 사용자: 1~10
		// [i][0] = 영화제목, [i][1]=NAVER코드, [i][2]=DAUM코드
		String[][] movieArr = new String[10][3];

		// 현재시간 계산
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		Date date = new Date();
		String time = dateFormat.format(date);
		System.out.println(time);

		// 프로그램 시작부 출력
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■■ MovieCrawler Ver1.2");
		System.out.println("■■ Developer: ");
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■■ Movie Ticketing Ranking(currentTime" + time + ")");

		// 네이버, 다음 1위 ~ 10위 영화 고유코드를 수집하는 URL
		String naverRank = "https://movie.naver.com/movie/running/current.nhn"; // 네이버 예매 1~10위
		String daumRank = "http://ticket2.movie.daum.net/Movie/MovieRankList.aspx"; // 다음 예매 1~10위

		// 네이버 영화 실시간 예매 순위 1위 ~ 10위까지 수집
		Document naverDoc = Jsoup.connect(naverRank).get();
		Elements naverList = naverDoc.select("dt.tit > a");

		// 네이버 영화 예매 순위 1~10위까지 추출
		for (int i = 0; i < 10; i++) {
			// 영화제목 = [i][0]
			movieArr[i][0] = naverList.get(i).text(); // 영화제목
			// href=/movie/bi/mi/basic.nhn?code=181925;
			String href = naverList.get(i).attr("href");
//			System.out.println(naverList.get(i).attr("href"));

			// substring(start, end); substring(0,10) 0~9슬라이스
			// substring(start); substring(5) 5~끝까지 슬라이스
			// indexOf("=") 내가 원하는 텍스트에 인덱스 번호를 리턴
			// -> 처음무터 찾음, 텍스트에 =이 3개 포함되어있으면
			// -> 첫번째 = 의 인덱스 번호를 리턴함
			// String str = "동선=동선=덩선";
			// str.indexOf("="); >>> 2
			// lastIndexOf() == 끝에서부터 검색
			movieArr[i][1] = href.substring(href.lastIndexOf("=") + 1);// 네이버
		}
		// 다음 영화 실시간 예매 순위 1위~위까지 수집
		Document daumDoc = Jsoup.connect(daumRank).get();
		Elements daumList = daumDoc.select("strong.tit_join > a");

		for (int j = 0; j < 10; j++) {
			String url = daumList.get(j).attr("href");
			// 순위별
			Document detailMovie = Jsoup.connect(url).get();
			String href = detailMovie.select("div.wrap_pbtn > a").attr("href");
			movieArr[j][2] = href.substring(href.lastIndexOf("=") + 1); // 다음영화 코드
		}

		// 개발: 배열(영화제목, NAVER코드, DAUM코드) 출력
//		for (int j = 0; j < movieArr.length; j++) {
//			for(int k = 0; k < 3; k++) {
//				System.out.println(movieArr[j][k] + "\t");
//			}
//			System.out.println();
//		}

		// 프로그램 영화랭킹 (1~10위) 출력
		for (int i = 0; i < movieArr.length; i++) {
			System.out.println("■■" + (i + 1) + "위\t" + movieArr[i][0]);
		}
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■■ Please enter a movie rank");

		// 사용자 입력값 유효성 체크

		while (true) {
			System.out.print("■■ >>");
			selCode = sc.nextInt();

			if (selCode >= 1 && selCode <= 10) {
				break;
			} else {
				System.out.println("다시 입력해주세요");
				continue;
			}
		}
		// 실제 사용자가 입력한 영화 수집 시작!!!(네이버, 다음)
		NaverTicket nTicket = new NaverTicket();
		TicketDTO nDto = nTicket.naverCrwaler(movieArr[selCode - 1][0],movieArr[selCode - 1][1] );
		// 다음 수집
		DaumTicket dTicket = new DaumTicket();
		TicketDTO dDto = dTicket.daumCrwaler(movieArr[selCode - 1][0],movieArr[selCode - 1][2] );
		
		// nDto => 네이버 댓글수, 평점의 합
		// dDto => 다음 댓글수, 평점의 합
		
		// 프로그램 수집결과 분석
		int nCnt = nDto.getCount(); // 네이버 댓글 수
		int dCnt = dDto.getCount(); // 다음 댓글 수
		int totalCnt = nCnt + dCnt;
		
		int nSum = nDto.getTotal(); // 네이버 평점합
		int dSum = dDto.getTotal(); // 다음 평점합
		int totalSum = nSum + dSum; // 네이버 + 다음 평점합
		
		DecimalFormat df = new DecimalFormat("0.0");
		double nAvg = (double)nSum/nCnt; // 네이버 평점 평균
		double dAvg = (double)dSum/dCnt; // 다음 평점 평균
		double totalAvg = (double)totalSum/totalCnt; //네이버 + 다음 평점 평균
		
		
		
		// 플로그램 수집결과 출력 
		String report = ""; //분석결과 저장
		if(totalAvg >= 8 && totalAvg <= 10) {
			report = "꼭 보세요";
		} else if(totalAvg >= 6 && totalAvg < 8) {
			report = "재미있는 평작";
		} else if(totalAvg >= 4 && totalAvg < 6) {
			report = "킬링타임용!";
		} else if(totalAvg >= 2 && totalAvg < 4) {
			report = "시간 아깝습니다";
		} else {
			report = "이걸 보신다구요? 미치셨습니까??";
		}
		
		//프로그램 분석결과 출력
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ Collection completed :)");
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("■ Summary");
		System.out.println("■ NAVER Count: " + nCnt);
		System.out.println("■ NAVER Sum" + nSum);
		System.out.println("■ NAVER Avg" + df.format(nAvg));
		System.out.println("■ =====================================================");
		System.out.println("■ DAUM Count: " + dCnt);
		System.out.println("■ DAUM Sum: " + dSum);
		System.out.println("■ DAUM Avg: " + df.format(dAvg));
		System.out.println("■ =====================================================");
		System.out.println("■ Total Reply Count: " + totalCnt);
		System.out.println("■ Total Sum: " + totalSum);
		System.out.println("■ Total Reply Avg: " + df.format(totalAvg));
		System.out.println("■ =====================================================");
		System.out.println("■ 2.Report :)");
		System.out.println("■ '" +movieArr[selCode-1][0] + "'수집 및 분석결과");
		System.out.println("■ 전체 댓글 수 "+totalCnt+", 평균평점 " +df.format(totalAvg)+ "로");
		System.out.println("■ "+report+ "영화 입니다.");
		System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
	}
	
	
}
