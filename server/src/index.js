var koa = require('koa');
var app = module.exports = new koa();
const server = require('http').createServer(app.callback());
const WebSocket = require('ws');
const wss = new WebSocket.Server({server});
const Router = require('koa-router');
const cors = require('@koa/cors');
const bodyParser = require('koa-bodyparser');

app.use(bodyParser());

app.use(cors());

app.use(middleware);

function middleware(ctx, next) {
  const start = new Date();
  return next().then(() => {
    const ms = new Date() - start;
    console.log(`${start.toLocaleTimeString()} ${ctx.request.method} ${ctx.request.url} - ${ms}ms`);
  });
}

const players = [];

const router = new Router();
router.get('/players', ctx => {
    ctx.response.body = players;
    ctx.response.status = 200;
  
});

const broadcast = (data) =>
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify(data));
    }
  });

router.post('/player', ctx => {
  const player = ctx.request.body;
  let issue;
  // if (!player.title) {
  //   issue = {title: 'Title is missing'};
  // }
  if (issue) {
    ctx.response.body = issue;
    ctx.response.status = 400;
  } else {
    // if (!player.date) {
    //   player.date = new Date();
    // }
    //lastUpdated = player.date;
    player.id = players.length + 1;
    players.push(player);
    console.log(players);
    ctx.response.body = player;
    ctx.response.status = 201;
    broadcast(player);
  }
});

router.put('/modifyPlayer', ctx => {
  const player = ctx.request.body;
  let issue;
  // if (!player.title) {
  //   issue = {title: 'Title is missing'};
  // }
  if (issue) {
    ctx.response.body = issue;
    ctx.response.status = 400;
  } else {
    // if (!player.date) {
    //   player.date = new Date();
    // }
    //lastUpdated = player.date;
    console.log(player)
    for(let i=0; i<players.length; i++){
      if(players[i].id == player.id){
        players[i] = player;
      }
    }
    console.log(players);
    ctx.response.body = player;
    ctx.response.status = 201;
    broadcast(player);
  }
});

router.delete('/deletePlayer', ctx => {
  const player = ctx.request.body;
  console.log(player.id)
  let issue;
  // if (!player.title) {
  //   issue = {title: 'Title is missing'};
  // }
  if (issue) {
    ctx.response.body = issue;
    ctx.response.status = 400;
  } else {
    // if (!player.date) {
    //   player.date = new Date();
    // }
    //lastUpdated = player.date;
    console.log(player.id)
    for(let i=0; i<players.length; i++){
      if(players[i].id == player.id){
        players.splice(i, 1);
      }
    }
    console.log(players);
    ctx.response.body = player;
    ctx.response.status = 201;
    broadcast(player);
  }
});

app.use(router.routes());
app.use(router.allowedMethods());

server.listen(3000);