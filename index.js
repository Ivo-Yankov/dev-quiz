var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var unirest = require('unirest');

var game_is_running = false;
var player_names = [];
var questions;
var categories;
var category;

getCategoryList();

app.get('/', function(req, res){
	res.sendFile(__dirname + '/index.html');
});


//TOVA NE E TAKA
app.get('/client', function(req, res){
	// Send shit to the client
	if( ! game_is_running ) {
		res.send('lqlqlq');
	}
	else {
		res.send('igrata e zapo4nala');
	}
});

io.on('connection', function(socket){

	console.log('new connection! ' + socket.id);
	socket.on('chat message', function(msg){
		
	});

	socket.on('start game', function(data){

		game_is_running = true;
	});

	socket.on('name', function(name){
		//save the name
		console.log('A player has set their name to: ' + name);
		player_names[socket.id] = name;

		//Send status to the client
		io.emit('status', {'status' : 'OK'});
	});
});

http.listen(3000, function(){
	console.log('listening on *:3000');
});

function getQuestions(){
	category = categories[Math.floor( Math.random() * (categories.length) )];
	var category_id = category.id;
	//TODO remove used category

	unirest.get("https://pareshchouhan-trivia-v1.p.mashape.com/v1/getQuizQuestionsByCategory?categoryId=" + category_id + "&limit=3&page=" + Math.floor(Math.random() * 3) )
	.header("X-Mashape-Key", "rCHR14UA0imshTfNn7uYk7sMByXCp1YTB2Ijsny27oo25nA7oB")
	.header("Accept", "application/json")
	.end(function (result) {
		questions = result.body;
		console.log(result.body);
	});
}

function sendQuestion(){
	io.emit('question', {category : category_name, question : questions[ questions.length - 1 ] });
	questions.length.pop();
	if( questions.length == 0 ) {
		getQuestions( category_id );
	}
}

function getCategoryList(){
	unirest.get("https://pareshchouhan-trivia-v1.p.mashape.com/v1/getCategoryList")
	.header("X-Mashape-Key", "rCHR14UA0imshTfNn7uYk7sMByXCp1YTB2Ijsny27oo25nA7oB")
	.header("Accept", "application/json")
	.end(function (result) {
		getQuestions();
		categories = result.body;
		console.log(categories);
	});
}