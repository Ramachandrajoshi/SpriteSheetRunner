
[![](https://jitpack.io/v/Ramachandrajoshi/SpriteSheetRunner.svg)](https://jitpack.io/#Ramachandrajoshi/SpriteSheetRunner) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**SpriteSheetRunner**

Create 2d FPS animations using Sprite Sheets.

you can run the sprite sheet FPS animation by adding in xml layout file

**Install:**  
Add it in your root build.gradle at the end of repositories



     allprojects {  
          repositories {  
             ...  
             maven { url 'https://jitpack.io' }  
          }  
       }  


Add the dependency  in app level build.gradle



     dependencies {  
               ...  
               implementation 'com.github.Ramachandrajoshi:SpriteSheetRunner:1.0.0'  
       }  



Usage:

       <com.codyzen.spriterunner.SpriteView  
           android:layout_width="match_parent"  
           android:layout_height="match_parent"  
           app:columns="8"  
           app:fps="8"  
           app:rows="1"  
           app:lastFrame="6"  
           app:src="@drawable/capguy" />  

API's available:

app:src ----  sprite sheet in image format

app:columns ----  number of columns in sheet (default 4)

app:rows  -----  number of rows in sheet (default 4)

app:fps  ----- max frame to loop through (default rows * columns)

app:lastFrame  ----- max frame to loop through (default fps)

app:isFixedRow  ----- render only one fixed row or not (default false)

app:renderRow  ----- which row to render. works only if isFixedrow is set true

app:autoPlay  ----- auto play or not. (default true) you can start or stop in class side as well


callback:

stateChangeListener: StateChangeListener  ----- for update start and stop callbacks. all methods or optional. implement which one you want



**demo**

![Output sample](https://github.com/Ramachandrajoshi/SpriteSheetRunner/raw/master/demo/demo.gif)