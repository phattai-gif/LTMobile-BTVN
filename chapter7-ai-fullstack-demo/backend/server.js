const app = require('./app');
const PORT = 5000;

app.listen(PORT, () => {
  console.log(`Backend Server đang chạy tại http://localhost:${PORT}`);
});
