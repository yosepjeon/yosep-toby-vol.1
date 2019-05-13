package io.yosep.toby.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException {
//		BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
//			
//			@Override
//			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//				// TODO Auto-generated method stub
//				Integer sum = 0;
//				String line = null;
//				
//				while((line = br.readLine()) != null) {
//					sum += Integer.valueOf(line);
//				}
//				
//				return sum;
//			}
//		};
//		
//		return fileReadTemplate(filepath, sumCallback);
		
		LineCallback<Integer> sumCallback = 
				new LineCallback<Integer>() {
					
					@Override
					public Integer doSomethingWithLine(String line, Integer value) {
						// TODO Auto-generated method stub
						return value + Integer.valueOf(line);
					}
				};
				return lineReadTemplate(filepath,sumCallback,0);
	}
	
	public Integer calcMultiply(String filepath) throws IOException {
//		BufferedReaderCallback multiplyCallback = new BufferedReaderCallback() {
//			
//			@Override
//			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//				// TODO Auto-generated method stub
//				Integer multiply = 1;
//				String line = null;
//				
//				while((line = br.readLine()) != null) {
//					multiply += Integer.valueOf(line);
//				}
//				
//				return multiply;
//			}
//		};
//		
//		return fileReadTemplate(filepath, multiplyCallback);
		
		LineCallback<Integer> multiplyCallback = 
				new LineCallback<Integer>() {
					
					@Override
					public Integer doSomethingWithLine(String line, Integer value) {
						// TODO Auto-generated method stub
						return value * Integer.valueOf(line);
					}
				};
				return lineReadTemplate(filepath,multiplyCallback,1);
	}
	
	public String concatenate(String filepath) throws IOException {
		LineCallback<String> concatenateCallback = 
				new LineCallback<String>() {

					@Override
					public String doSomethingWithLine(String line, String value) {
						// TODO Auto-generated method stub
						return value + line;
					}
				};
				
				return lineReadTemplate(filepath, concatenateCallback, "");
	}
	
	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filepath));
			int ret = callback.doSomethingWithReader(br);
			return ret;
		}catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) {
				try {
					br.close();
				}catch(IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
			String line = null;
			
			while((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			
			return res;
		}catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) {
				try {
					br.close();
				}catch(IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
}
