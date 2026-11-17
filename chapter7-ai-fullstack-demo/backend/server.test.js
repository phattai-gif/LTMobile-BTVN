const request = require('supertest');
const app = require('./app');

describe('Integration Tests cho API (Chapter 7)', () => {
  it('GET /api/contacts - Trả về danh sách liên lạc', async () => {
    const res = await request(app).get('/api/contacts');
    expect(res.statusCode).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
  });

  it('POST /api/contacts - Thêm mới thành công khi dữ liệu hợp lệ', async () => {
    const res = await request(app)
      .post('/api/contacts')
      .send({ name: 'Lê Thị B', email: 'lethib@utc2.edu.vn' });
    expect(res.statusCode).toBe(201);
    expect(res.body.name).toBe('Lê Thị B');
  });

  it('POST /api/contacts - Báo lỗi 400 nếu bỏ trống tên (Validation)', async () => {
    const res = await request(app)
      .post('/api/contacts')
      .send({ name: '', email: 'test@utc2.edu.vn' });
    expect(res.statusCode).toBe(400);
  });

  it('POST /api/transfer - Tự động Rollback khi chuyển vượt số dư', async () => {
    const res = await request(app)
      .post('/api/transfer')
      .send({ fromId: 2, toId: 1, amount: 999999 });
    expect(res.statusCode).toBe(400);
    expect(res.body.error).toContain('Insufficient funds');
  });
});
