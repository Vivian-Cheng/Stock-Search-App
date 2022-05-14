<h1 style="text-align: center;">Stock Search App</h1>
<p style="text-align: center;">
 An Android application that allows you to search for stock information, follow your favorite stock, and simulate stock transactions.   
</p>
<p style="text-align: center;">
   <a href="https://github.com/Vivian-Cheng/Stock-Search-App">Explore the docs >></a> • 
    <a href="https://youtu.be/mjExpHsQNAg">See demo >></a>
</p>

![](/image/cover.png)

### Table of Contents
* [Feature](#Feature)
* [Built With](#Built-With)
* [Getting Start](#Getting-Start)
* [Contact](#Contact)

## Feature
* **Autocomplete suggestions** - enter a keyword, populate a dropdown selection of suggested stock symbols for a simple search
* **Real-time stock prices and stock quotes** - show the latest stock quotes for a financial overview
* **Insight of stock analysis** - view historical data, recommendation trends, social sentiment, and the company's earnings
* **Up-to-date news** - watch the latest business and finance news
* **Portfolio management** - buy and sell stock through a virtual wallet
* **Watchlist management** - keep track of your favorite stocks by adding them to the watchlist

## Built With
- [Node.js](https://nodejs.org/en/)
- [Express.js](https://expressjs.com)
- [Android](https://developer.android.com/studio/)
- [Java](https://www.java.com/)
- [Highcharts](https://www.highcharts.com)
- [Finnhub Stock API](https://finnhub.io)

## Getting Start

### Prerequisites
* Install Node.js and npm
    ```sh
    $ npm install -g npm
    ```
* Install [Android Studio](https://developer.android.com/studio/index.html)
### Installation
1. Get a free API Key at https://finnhub.io
2. Clone the repo
    ```sh
    $ git clone https://github.com/Vivian-Cheng/Stock-Search-App.git
    ```
3. Install NPM packages in `Backend` repository
    ```sh
    $ npm install
    ```
4. Enter your API Key in `Backend/controllers/comp_controller.js`
    ```js
    const token = "{API_KEY}";
    ```
5. Run the app in `Backend` repository
    ```sh
    node app.js
    ```
6. Open Android Studio and run the emulator(preferably Pixel 3A with API 30)
## Contact
* Vivian Cheng - vivian0422.c@gmail.com
* Project Link - https://github.com/Vivian-Cheng/Stock-Search-App
