# EasySeekBar
一个简单的SeekBar，支持设置每个分段点的文字，自动吸附到附近的点

<img src="./demo.png?raw=true" alt="Example" width="270" />

## 依赖：

[![](https://jitpack.io/v/goyourfly/EasySeekBar.svg)](https://jitpack.io/#goyourfly/EasySeekBar)


Step 1. Add the JitPack repository to your build file

````
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	
````
Step 2. Add the dependency

````
	dependencies {
	        compile 'com.github.goyourfly:EasySeekBar:-SNAPSHOT'
	}
````

## 用法：

````xml
    <com.goyourfly.easyseekbar.EasySeekBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:paddingBottom="16dp"
        android:paddingTop="16dp" 
        app:xxx="xxx"/>
````

## 可自定义参数：
| 参数 | 类型 | 意思 |
|-----|:-----:|:------:|
|bar_radius|dp|Bar的半径|
|second_bar_radius|dp|灰色的Bar半径|
|text_size|sp|文字大小|
|text_margin|dp|文字距底部距离|
|text_color|color|文字颜色|
|line_height|dp|线条宽度|
|default_color|color|默认颜色值|
|highlight_color|color|高亮颜色|
|values|stringArray|文字内容|
