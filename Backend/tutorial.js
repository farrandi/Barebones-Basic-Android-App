var express = require("express");
const { request } = require("http");
var app = express();
const fetch = require("node-fetch");

const { MongoClient } = require("mongodb");
const uri = "mongodb://localhost:27017";
const client = new MongoClient(uri);

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

// app.post("/", (req, res) => {
//   res.send(req.body.text);
// });

//To do list
// app.get("/todolist", async (req, res) => {
//   try {
//     const result = await client
//       .db("test")
//       .collection("todolist")
//       .find(req.body);
//     await result.forEach(console.dir);
//     res.status(200).send("Items retrieved\n");
//   } catch (err) {
//     console.log(err);
//     res.status(400).send(err);
//   }
// });
// app.post("/todolist", async (req, res) => {
//   try {
//     await client.db("test").collection("todolist").insertOne(req.body);
//     res.status(200).send("To do item added successfully\n");
//   } catch (err) {
//     console.log(err);
//     res.status(400).send(err);
//   }
// });
// app.put("/todolist", async (req, res) => {
//   try {
//     await client
//       .db("test")
//       .collection("todolist")
//       .replaceOne({ task: "Finish this tutorial" }, req.body);
//     res.status(200).send("To do item updated successfully\n");
//   } catch (err) {
//     console.log(err);
//     res.status(400).send(err);
//   }
// });
// app.delete("/todolist", async (req, res) => {
//   try {
//     await client
//       .db("test")
//       .collection("todolist")
//       .deleteOne({ task: req.body.task });
//     res.status(200).send("To do item deleted successfully\n");
//   } catch (err) {
//     console.log(err);
//     res.status(400).send(err);
//   }
// });

// Connect to mongoDB
async function run() {
  try {
    await client.connect();
    console.log("Successfully connected to db");
    // Launch Server @ port 5001
    var server = app.listen(5001, (req, res) => {
      var port = server.address().port;
      console.log(`successfully running at http://localhost:${port}`);
    });
  } catch (err) {
    console.log(err);
    await client.close();
  }
}

run();
