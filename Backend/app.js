var express = require('express');
var cors = require('cors');
var app = express();
var port = process.env.PORT || 3000;

var searchRouter = require('./routes/index');

app.use(cors());
app.use('/', searchRouter);
app.use(express.static(process.cwd() + "/app/dist/app/"));

server = app.listen(port);
console.log('Server running at http://127.0.0.1:' + port + '/');
/*
var server = app.listen(5050, function () {

  var host = server.address().address
  var port = server.address().port

  console.log("server boot", host, port);

})
*/
