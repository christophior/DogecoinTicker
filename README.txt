DogecoinTicker

Chris Vilarreal - hcv98
Zach Zador - zaz78

Upon opening the app, it will take up to roughly 10 seconds for the app to pull the current price of Dogecoin from all the different exchanges. You'll see this happen when the prices change from "0.0" to something else. You can then click on one of the exchanges to see a graph of recent trades, displaying the price they went for and the time at which it happened. (NOTE: CoinedUp does not currently work. Still waiting on API access). In the Settings menu, you can turn on ongoing notifications which will display the price of Dogecoin in microBTC for any echanges you choose. (NOTE: Currently only microBTC functions currently. Proer conversions need t o still be added for BTC and Satoshi).

Completed:
 - Initial exchange list displaying current prices
 - Graph displaying recent trades on that exchange
 - Settings to allow ongoing notifications of current price of Dogecoin on specific exchange

Not Completed:
 - Allow user to change range of graph
 - Implement Satoshi and BTC and allow the use of CoinedUp exchange upon receiving API key
 - Notifications when price reaches user set level
 - Coins-E and Vircurex graphs are just copies of Cryptsy
 - Currently, the graphs can result in odd looking shapes. We are still looking in to how to effectively parse from    each exchange's API in order to make sure all of the graphs are always accurate and readable to the user.

Besides the use of API's from various exchanges, the only major chunk of code used within the app from an external source was the initial code to use JSON to parse the API's. This was credited in our code, and can be found at http://www.androidhive.info/2012/01/android-json-parsing-tutorial/. Also, the sections from the Settings and Notification classes are heavily based off of the official Android Developer website.

All other classes were completed by us.
  