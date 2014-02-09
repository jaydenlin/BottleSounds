Heare
============

Y! Hack Day Project

* 更新Facebook SDK 至最新版 Facebook SDK 3.6 //2014-02-09

   * Step 0: 刪除原有Facebook SDK資料夾內容所有檔案 (eclipse workspace內、硬碟內檔案)，保留空資料夾
   * Step 1: 下載Facebook SDK 3.6 壓縮檔
   
   ```
   https://www.dropbox.com/s/iy16hmsny0jwyn5/facebook-android-sdk-3.6.zip
   ```

   * Step 2: 解壓縮後，將檔案複製至Step0之空資料夾內(FacebookSDK)，其餘照原import步驟重新做一遍 (請參考下方)
   

* 加入Facebook SDK到Heare專案裡
   * Step 1: 下載Facebook SDK 3.5.2，並且import到eclipse裡(import it as an Android Project)
   
   ```
   git clone git@github.com:jaydenlin/FacebookSDK.git
   ```
   
   * Step 2: 在到Heare右鍵=>Properties=>Android=>Library=>Add，選取剛才import的Facebook SDK
   * Step 3: 記得把執行的環境設定成Android 4.3哦
   
* 加入Google_Play_Service_SDK到Heare專案裡
  
  * Step 1: 下載google play service sdk，並且import到eclipse(import it as an Android Project) 
   ```
   git clone git@github.com:jaydenlin/Google_Play_Service_SDK.git
   ```

  * Step 2: 在到Heare右鍵=>Properties=>Android=>Library=>Add，選取剛才import的Google_Play_Service_SDK
  * Step 3: 記得把執行的環境設定成Android 4.3哦
    


   
