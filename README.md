# 受測驗者 - 簡大鈞


## 戲劇列表 資料流

* 檢查資料庫有無資料 -> 有，去 SharedPreference 拿前次搜尋項目並顯示搜尋結果 -> 無，則透過 Api 重新取得資料，若無法連線則嘗試重連 重試6次 每次間隔5秒

## 戲劇資訊 資料流

* 檢查 Intent是否有 Drama ，若無 則認為 Intent 從 AppLink 來， 取得戲劇 ID 後 先嘗試從 Local 取得戲劇，若無法取得，則嘗試從 API 取得資料並更新進 Local DB，
若無法連線則嘗試重連，重連 6 次，每次間隔 10 秒

------

**額外增加使用者體驗功能**

* 使用者搜尋之後並進去看戲劇資訊的話，該關鍵字紀錄在 SharedPrederence 作為歷史紀錄，點選搜尋編輯時會提示前 5 次的歷史紀錄

* 目前無資料、並且一進主畫面沒有成功的話，使用者仍可透過上拉到底加載資料進來

------

右上角點點所提供功能為測試使用，並非提供給使用者之功能

* 清空 Local DB

* 清空 SharedPreference

測試 APK 下載連結

https://drive.google.com/file/d/1mYscYifkS3aS5yilrkqqpbIaCfijarO2/view?usp=sharing
