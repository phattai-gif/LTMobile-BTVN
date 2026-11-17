import React, { useState, useEffect } from 'react';

/**
 * Component: TransferForm
 * Ý 2 trong sách: Tách module chuyển tiền nguyên tử (Atomic Transfer).
 * Ý 3 trong sách: Nhận props đồng bộ:
 *   - contacts: Array
 *   - onTransfer: (data) => Promise<void>
 *   - isTransferring: boolean
 */
export default function TransferForm({ contacts, onTransfer, isTransferring }) {
  const [fromId, setFromId] = useState('');
  const [toId, setToId] = useState('');
  const [amount, setAmount] = useState('100');

  // Đồng bộ chọn tài khoản mặc định khi danh sách contacts thay đổi
  useEffect(() => {
    if (contacts.length >= 2) {
      if (!fromId || !contacts.some(c => c.id === Number(fromId))) {
        setFromId(contacts[0].id.toString());
      }
      if (!toId || !contacts.some(c => c.id === Number(toId))) {
        setToId(contacts[1].id.toString());
      }
    }
  }, [contacts]);

  const handleSubmit = (e) => {
    e.preventDefault();
    onTransfer({
      fromId: Number(fromId),
      toId: Number(toId),
      amount: Number(amount)
    });
  };

  const selectedSender = contacts.find(c => c.id === Number(fromId));

  return (
    <div className="card">
      <div className="card-title">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="m16 3 4 4-4 4"></path>
          <path d="M20 7H4"></path>
          <path d="m8 21-4-4 4-4"></path>
          <path d="M4 17h16"></path>
        </svg>
        Chuyển khoản ví (Atomic)
      </div>
      <div className="card-subtitle">
        Bảo toàn số dư với giao dịch nguyên tử & tự động Rollback
      </div>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">
            Tài khoản nguồn {selectedSender && `(Số dư: $${selectedSender.balance})`}
          </label>
          <select
            className="form-select"
            value={fromId}
            onChange={e => setFromId(e.target.value)}
          >
            {contacts.map(c => (
              <option key={c.id} value={c.id}>
                {c.name} (${c.balance})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">Tài khoản đích</label>
          <select
            className="form-select"
            value={toId}
            onChange={e => setToId(e.target.value)}
          >
            {contacts.map(c => (
              <option key={c.id} value={c.id}>
                {c.name} (${c.balance})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">Số tiền chuyển ($)</label>
          <input
            type="number"
            className="form-input"
            placeholder="Nhập số tiền..."
            value={amount}
            onChange={e => setAmount(e.target.value)}
          />
          <div className="quick-amounts">
            <button type="button" className="amount-chip" onClick={() => setAmount('50')}>
              $50
            </button>
            <button type="button" className="amount-chip" onClick={() => setAmount('100')}>
              $100
            </button>
            <button type="button" className="amount-chip" onClick={() => setAmount('200')}>
              $200
            </button>
            <button
              type="button"
              className="amount-chip danger-test"
              title="Test số tiền lớn vượt số dư để xem Rollback"
              onClick={() => setAmount('5000')}
            >
              $5000 ⚠️
            </button>
          </div>
        </div>

        <button
          type="submit"
          className="btn-primary"
          style={{ background: '#0f766e' }}
          disabled={isTransferring || contacts.length < 2}
        >
          {isTransferring ? 'Đang giao dịch...' : 'Xác nhận chuyển tiền'}
        </button>
      </form>
    </div>
  );
}
