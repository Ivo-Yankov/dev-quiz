var express = require('express');
var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var unirest = require('unirest');
var path = require('path');

var game_is_running = false;
var players = {};
var questions;
var categories;
var category;
var current_question;

var question_is_answered = false;

getCategoryList();

app.get('/', function(req, res){
	if( ! game_is_running ){
		res.sendFile(__dirname + '/begin.html');
	}
	else {
		res.sendFile(__dirname + '/question.html');
	}
});

app.use("/lib", express.static(path.join(__dirname, 'lib')));
app.use("/css", express.static(path.join(__dirname, 'css')));

io.on('connection', function(socket){
	console.log('new connection! ' + socket.id);

	socket.on('start game', function(data){
		console.log('game is starting!');
		game_is_running = true;
		io.emit('start game', {'status' : 'OK'});
	});

	socket.on('game started', function(data){
		console.log('The game has started!');
		sendQuestion();
	});

	socket.on('name', function(name){
		//save the name
		console.log('A player has set their name to: ' + name);
		players[socket.id] = { name: name, points: 0 };

		//Send status to the client
		io.emit('status', {'status' : 'OK'});
	});

	socket.on('answer', function(answer){
		console.log(question_is_answered);
		if ( !question_is_answered && players[socket.id] ) {
			question_is_answered = true;
			var status;

			if(answer == current_question.q_correct_option){
				status = true;
				players[socket.id].points++;
			}
			else{
				status = false;
				players[socket.id].points--;
			}
			
			console.log(players);

			io.emit('answer', {
				'status'	: status,
				'player'	: players[socket.id].name || 'undefined',
				'answer'	: answer,
				'players'	: players
			});

			setTimeout(function(){
				sendQuestion();
			}, 3000);
		}
	});
});

http.listen(3000, function(){
	console.log('listening on *:3000');
});

function getQuestions(){
		category = categories[Math.floor( Math.random() * (categories.length) )];
		var category_id = category.id;
		//TODO remove used category

		unirest.get("https://pareshchouhan-trivia-v1.p.mashape.com/v1/getQuizQuestionsByCategory?limit=10&page=" + Math.floor(Math.random() * 3) )
		.header("X-Mashape-Key", "rCHR14UA0imshTfNn7uYk7sMByXCp1YTB2Ijsny27oo25nA7oB")
		.header("Accept", "application/json")
		.end(function (result) {
			console.log('new questions are here');
			questions = result.body;
			shuffle(questions);
		});
}

function sendQuestion(){
	console.log('sending question');
	question_is_answered = false;
	current_question = questions[ questions.length - 1 ];
	
	// var question_data = {
	// 	category : category.category_name,
	// 	question : current_question
	// };

	var question_data = current_question;

	io.emit('question', question_data);
	questions.pop();
	if( questions.length == 2 ) {
		console.log('getting new questions');
		getQuestions();
	}
}

function getCategoryList(){
	unirest.get("https://pareshchouhan-trivia-v1.p.mashape.com/v1/getCategoryList")
	.header("X-Mashape-Key", "rCHR14UA0imshTfNn7uYk7sMByXCp1YTB2Ijsny27oo25nA7oB")
	.header("Accept", "application/json")
	.end(function (result) {
		categories = result.body;
		getQuestions();
		console.log(categories);
	});
}

function shuffle(a) {
    var j, x, i;
    for (i = a.length; i; i -= 1) {
        j = Math.floor(Math.random() * i);
        x = a[i - 1];
        a[i - 1] = a[j];
        a[j] = x;
    }
}