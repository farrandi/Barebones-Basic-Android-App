var express = require("express");
const { request } = require("http");
var app = express();
const fetch = require("node-fetch");

// Middleware
app.use(express.json());

// API functions
app.get("/", (req, res) => {
  res.send("Hello World!");
});

app.get("/ip", async (req, res) => {
  //   API to return public IP address of server
  //   https://checkip.amazonaws.com/
  try {
    const apiResponse = await fetch("https://checkip.amazonaws.com/");
    const ip = await apiResponse.text();
    res.send(ip);
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
});

app.get("/name", (req, res) => {
  res.send("Farrandi Hernando");
});

app.get("/time", (req, res) => {
  // API to return server local time

  let date_ob = new Date();
  // current date
  // adjust 0 before single digit date
  let date = ("0" + date_ob.getDate()).slice(-2);

  // current month
  let month = ("0" + (date_ob.getMonth() + 1)).slice(-2);

  // current year
  let year = date_ob.getFullYear();

  // current hours
  let hours = date_ob.getHours();

  // current minutes
  let minutes = date_ob.getMinutes();

  // current seconds
  let seconds = date_ob.getSeconds();

  res.send(
    year +
      "-" +
      month +
      "-" +
      date +
      " " +
      hours +
      ":" +
      minutes +
      ":" +
      seconds
  );
});

// Launch Server @ port 5001
function run() {
  try {
    var server = app.listen(5001, (req, res) => {
      var port = server.address().port;
      console.log(`successfully running at http://localhost:${port}`);
    });
  } catch (err) {
    console.log(err);
  }
}

run();

// nohup node server.js &

// ssh -i "cpen321.cer" ubuntu@ec2-34-219-244-223.us-west-2.compute.amazonaws.com
