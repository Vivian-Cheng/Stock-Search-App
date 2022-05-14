var https = require('https');
const { resolve } = require('path');
const token = "{API_KEY}";

function requester(url) {
  return new Promise((resolve) => {
    https.get(url, (res) => {
      let data = '';
      res.on('data', chunk => { data += chunk });
      res.on('end', () => {
        resolve(data);
      });
    }).on("error", (err) => {
      reject(err.message);
    });
  })
}

// GET home page
exports.index = function (req, res) {
  res.sendFile(process.cwd() + "/app/dist/app/index.html");
};

// GET general information of a company
exports.comp_descript = async function (req, res) {
  let symbol = req.params.ticker;
  let url = "https://finnhub.io/api/v1/stock/profile2?symbol=" + symbol + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET company historical data
exports.comp_candle = async function (req, res) {
  let symbol = req.params.ticker;
  let resolution = req.params.resolution;
  let from = req.params.from;
  let to = req.params.to;
  //let resolution = "D";
  //let date = new Date();
  //date.setFullYear(date.getFullYear() - 2);
  //let from = Math.floor(date / 1000); // UNIX timestamp: 2 years prior to date today
  //let to = Math.floor(Date.now() / 1000); // UNIX timestamp: date today
  let url = "https://finnhub.io/api/v1/stock/candle?symbol=" + symbol + "&resolution=" + resolution + "&from=" + from + "&to=" + to + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET latest price of stock
exports.comp_quote = async function (req, res) {
  let symbol = req.params.ticker;
  let url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET autocomplete
exports.comp_autocomplete = async function (req, res) {
  let query = req.params.q;
  let url = "https://finnhub.io/api/v1/search?q=" + query + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET company’s news of the last 7 days
exports.comp_news = async function (req, res) {
  let symbol = req.params.ticker;
  let date = new Date();
  let to = date.toISOString().split('T')[0]; // YYYY-MM-DD: date today
  date.setDate(date.getDate() - 7);
  let from = date.toISOString().split('T')[0]; // YYYY-MM-DD: 1 year prior to date today
  let url = "https://finnhub.io/api/v1/company-news?symbol=" + symbol + "&from=" + from + "&to=" + to + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET company’s recommendation trends
exports.comp_recommendation = async function (req, res) {
  let symbol = req.params.ticker;
  let url = "https://finnhub.io/api/v1/stock/recommendation?symbol=" + symbol + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET company’s social sentiment
exports.comp_social = async function (req, res) {
  let symbol = req.params.ticker;
  let date = new Date(new Date().getFullYear(), 0, 1);
  let from = date.toISOString().split('T')[0]; // 2022-01-01
  let url = "https://finnhub.io/api/v1/stock/social-sentiment?symbol=" + symbol + "&from=" + from + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET company’s peers
exports.comp_peers = async function (req, res) {
  let symbol = req.params.ticker;
  let url = "https://finnhub.io/api/v1/stock/peers?symbol=" + symbol + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}

// GET company’s earnings
exports.comp_earnings = async function (req, res) {
  let symbol = req.params.ticker;
  let url = "https://finnhub.io/api/v1/stock/earnings?symbol=" + symbol + "&token=" + token;
  let data = await requester(url);
  res.send(JSON.parse(data));
}
