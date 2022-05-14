var express = require('express');
var router = express();

var comp_controller = require('../controllers/comp_controller');

// GET homepage
//router.get('/home', comp_controller.index);

// GET homepage
//router.get('/:ticker', comp_controller.index);

router.get('/', comp_controller.index);
router.get('/search/home', comp_controller.index);


// GET request for company description
router.get('/descript/:ticker', comp_controller.comp_descript);

// GET request for company historical data of the last 2 years
router.get('/candle/:ticker/:resolution/:from/:to', comp_controller.comp_candle);

// GET request for latest price of stock
router.get('/quote/:ticker', comp_controller.comp_quote);

// GET request for autocomplete
router.get('/q/:q', comp_controller.comp_autocomplete);

// GET request for company news of the last 1 year
router.get('/news/:ticker', comp_controller.comp_news);

// GET request for company recommendation trends
router.get('/recommendation/:ticker', comp_controller.comp_recommendation);

// GET request for company social sentiment
router.get('/sentiment/:ticker', comp_controller.comp_social);

// GET request for company peers
router.get('/peers/:ticker', comp_controller.comp_peers);

// GET request for company earnings
router.get('/earnings/:ticker', comp_controller.comp_earnings);
module.exports = router;
