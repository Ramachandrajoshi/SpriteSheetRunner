
[![](https://jitpack.io/v/Ramachandrajoshi/SpriteSheetRunner.svg)](https://jitpack.io/#Ramachandrajoshi/SpriteSheetRunner)  
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

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


**demo**

![Output sample](https://github.com/Ramachandrajoshi/SpriteSheetRunner/raw/master/demo/demo.gif)

**spriteSheet credits:**  
https://www.codeandweb.com/texturepacker/tutorials/animate-sprites-in-css-with-texturepacker  
https://gamedevelopment.tutsplus.com/tutorials/an-introduction-to-spritesheet-animation--gamedev-13099  
http://gaurav.munjal.us/Universal-LPC-Spritesheet-Character-Generator/#?sex=male&bracers=none&clothes=longsleeve_brown&eyes=red&nose=big&jacket=tabard&legs=robe_skirt
Unity 2d kit