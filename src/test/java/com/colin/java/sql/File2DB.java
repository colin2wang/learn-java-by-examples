package com.colin.java.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class File2DB {
	
	public static void main(String[] args) throws Exception {
		FileReader fr = new FileReader(new File("D:/Work/java/Cici/Part/src/trackData1.txt"));
		BufferedReader bin = new BufferedReader(fr);
		Connection connection = ConnectionHelper.openSQLServer("localhost:1433", "part", "sa", "19840516");
		
		PreparedStatement statment1 = connection.prepareStatement("insert into dbo.TrackData values(?, ?, ?, ?)");
		PreparedStatement statment2 = connection.prepareStatement("insert into dbo.TrackDataOptionalGenreId values(?, ?)");
		
		String temp = bin.readLine();
		
		while (temp != null) {
			StringBuffer sb = new StringBuffer();
			
			String values[] = temp.split("\\|");			
			statment1.setString(1, values[0]);
			statment1.setString(2, values[1]);
			statment1.setString(3, values[2]);
			
			if (values.length >= 4) {
				int index = 0;
				for (int i=3; i<values.length; i++) {
					statment2.setString(1, values[0]);
					statment2.setString(2, values[i]);
					statment2.execute();
					if (index++ >= 1) {
						sb.append("|");
					}
					sb.append(values[i]);
				}
			}
			
			statment1.setString(4, sb.toString());
			statment1.execute();
			
			temp = bin.readLine();
		}
	}
}
