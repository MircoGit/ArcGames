$(document).ready(function(){
	//Canvas
	var canvas = $("#canvas")[0];
	var ctx = canvas.getContext("2d");
	var width = $("#canvas").width();
	var height = $("#canvas").height();

	var cw = 10; //cell width
	var direction;
	var food;
	var score;

	var snake_array; //an array of cells to make up the snake
	
	function init()
	{
		direction = "right"; //default direction
		createSnake();
		createFood(); 
		
		score = 0;
		
		//A timer which will trigger the paint function every 60ms
		if(typeof game_loop != "undefined") clearInterval(game_loop);
		game_loop = setInterval(paint, 60);
	}
	init();
	
	function createSnake()
	{
		var length = 5; //Length of the snake
		snake_array = [];
		for(var i = length-1; i>=0; i--)
		{
			//This will create a horizontal snake starting from the top left
			snake_array.push({x: i, y:0});
		}
	}
	
	function createFood()
	{
		food = {
			x: Math.round(Math.random()*(width-cw)/cw), 
			y: Math.round(Math.random()*(height-cw)/cw), 
		};
	}

	function paint()
	{
		ctx.fillStyle = "white";
		ctx.fillRect(0, 0, width, height);
		ctx.strokeStyle = "black";
		ctx.strokeRect(0, 0, width, height);
		
		var nx = snake_array[0].x;
		var ny = snake_array[0].y;

		if(direction == "right") nx++;
		else if(direction == "left") nx--;
		else if(direction == "up") ny--;
		else if(direction == "down") ny++;

		if(nx == -1 || nx == width/cw || ny == -1 || ny == height/cw || check_collision(nx, ny, snake_array))
		{
			//restart game
			init();
			return;
		}

		if(nx == food.x && ny == food.y)
		{
			var tail = {x: nx, y: ny};
			score++;
			
			createFood(); //Create new food
		}
		else
		{
			var tail = snake_array.pop();
			tail.x = nx; tail.y = ny;
		}
		
		snake_array.unshift(tail); 
		
		for(var i = 0; i < snake_array.length; i++)
		{
			var c = snake_array[i];
			paint_cell(c.x, c.y);
		}

		paint_cell(food.x, food.y);

		var score_text = "Score: " + score;
		ctx.fillText(score_text, 5, height-5);
	}

	function paint_cell(x, y)
	{
		ctx.fillStyle = "blue";
		ctx.fillRect(x*cw, y*cw, cw, cw);
		ctx.strokeStyle = "white";
		ctx.strokeRect(x*cw, y*cw, cw, cw);
	}
	
	function check_collision(x, y, array)
	{
		//This function will check if the provided x/y coordinates exist
		//in an array of cells or not
		for(var i = 0; i < array.length; i++)
		{
			if(array[i].x == x && array[i].y == y)
			 return true;
		}
		return false;
	}
	
	$(document).keydown(function(e){
		var key = e.which;
		
		if(key == "37" && direction != "right") direction = "left";
		else if(key == "38" && direction != "down") direction = "up";
		else if(key == "39" && direction != "left") direction = "right";
		else if(key == "40" && direction != "up") direction = "down";
	})
})