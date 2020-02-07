package mybatis;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

// Builder를 통해 Factory를 하나 만들어주는 역할
public class SqlMapConfig {
	private static SqlSessionFactory sqlSessionFactory;
	
	// 정적 블럭
	// static을 정적 블럭이라 한다
	// 클래스 로딩시 1회만 실행되는 코드
	static {
		String resource = "mybatis/Configuration.xml";
		
		try {
			// Resources = java IO클래스
			// mybatis/Configuration.xml 파일가서 한줄씩 읽어서 reader에 집어넣어라
			Reader reader = Resources.getResourceAsReader(resource);
			
			if(sqlSessionFactory == null) {
				// new SqlSessionFactoryBuilder().build(reader)
				// 이런걸 빌드 패턴이라고 한다
				// 프레임워크라서 너무 양이 방대해
				// 객체생성 하기 버거워서 전문가에게 객체생성을 대신 해달라고 하는것
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
				// 5가지패턴을 가지고 전문가에게 빌드해주세요 하는것
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static SqlSessionFactory getSqlSession() {
		return sqlSessionFactory;
	}
	
}
