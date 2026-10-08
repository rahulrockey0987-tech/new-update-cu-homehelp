const http = require('http');
const app = require('../server');

const PORT = 5055;
const server = app.listen(PORT, async () => {
  console.log(`Test server running on port ${PORT}...`);

  const request = (method, path, body = null, token = null) => {
    return new Promise((resolve, reject) => {
      const data = body ? JSON.stringify(body) : null;
      const headers = { 'Content-Type': 'application/json' };
      if (data) headers['Content-Length'] = Buffer.byteLength(data);
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const req = http.request(
        {
          hostname: '127.0.0.1',
          port: PORT,
          path,
          method,
          headers,
        },
        (res) => {
          let responseBody = '';
          res.on('data', (chunk) => (responseBody += chunk));
          res.on('end', () => {
            try {
              resolve({ status: res.statusCode, body: JSON.parse(responseBody) });
            } catch (e) {
              resolve({ status: res.statusCode, body: responseBody });
            }
          });
        }
      );

      req.on('error', reject);
      if (data) req.write(data);
      req.end();
    });
  };

  try {
    console.log('\n--- 1. Testing POST /api/auth/register ---');
    const regRes = await request('POST', '/api/auth/register', {
      name: 'Priya Reddy',
      phone: '9848012345',
      email: 'priya.reddy@example.com',
      password: 'password123',
      confirmPassword: 'password123',
      address: 'Jubilee Hills Road 36, Hyderabad 500033',
      termsAccepted: true,
    });
    console.log('Register response status:', regRes.status, 'OTP sent:', regRes.body.otp);
    if (regRes.status !== 200 || !regRes.body.otp) throw new Error('Registration failed');

    console.log('\n--- 2. Testing POST /api/auth/verify-otp ---');
    const verifyRes = await request('POST', '/api/auth/verify-otp', {
      phone: '9848012345',
      otp: regRes.body.otp,
    });
    console.log('Verify OTP status:', verifyRes.status, 'Token received:', !!verifyRes.body.token);
    if (verifyRes.status !== 201 || !verifyRes.body.token) throw new Error('Verify OTP failed');
    const customerToken = verifyRes.body.token;

    console.log('\n--- 3. Testing GET /api/auth/me (Protected Route with JWT) ---');
    const meRes = await request('GET', '/api/auth/me', null, customerToken);
    console.log('Protected /me status:', meRes.status, 'Customer name:', meRes.body.customer?.name);
    if (meRes.status !== 200 || meRes.body.customer?.phone !== '9848012345') throw new Error('GET /api/auth/me failed');

    console.log('\n--- 4. Testing GET /api/auth/me without token (Should be 401 Unauthorized) ---');
    const unauthRes = await request('GET', '/api/auth/me', null, null);
    console.log('Unauthenticated access status:', unauthRes.status);
    if (unauthRes.status !== 401) throw new Error('Protected route should return 401');

    console.log('\n--- 5. Testing POST /api/auth/login ---');
    const loginRes = await request('POST', '/api/auth/login', {
      phoneOrEmail: '9848012345',
      password: 'password123',
    });
    console.log('Login status:', loginRes.status, 'Logged in customer:', loginRes.body.customer?.name);
    if (loginRes.status !== 200 || !loginRes.body.token) throw new Error('Login failed');

    console.log('\n--- 6. Testing Seeded Customer Login (9876543210 / 1234) ---');
    const seedLoginRes = await request('POST', '/api/auth/login', {
      phoneOrEmail: '9876543210',
      password: '1234',
    });
    console.log('Seeded customer login status:', seedLoginRes.status, 'Name:', seedLoginRes.body.customer?.name);
    if (seedLoginRes.status !== 200) throw new Error('Seeded customer login failed');

    console.log('\n--- 7. Testing POST /api/auth/forgot-password & reset-password ---');
    const forgotRes = await request('POST', '/api/auth/forgot-password', {
      phoneOrEmail: '9848012345',
    });
    console.log('Forgot password status:', forgotRes.status, 'Reset OTP:', forgotRes.body.otp);

    const resetRes = await request('POST', '/api/auth/reset-password', {
      phoneOrEmail: '9848012345',
      otp: forgotRes.body.otp,
      newPassword: 'newSecretPassword99',
      confirmPassword: 'newSecretPassword99',
    });
    console.log('Reset password status:', resetRes.status, 'Message:', resetRes.body.message);
    if (resetRes.status !== 200) throw new Error('Reset password failed');

    console.log('\n--- 8. Testing Login with New Password ---');
    const newLoginRes = await request('POST', '/api/auth/login', {
      phoneOrEmail: '9848012345',
      password: 'newSecretPassword99',
    });
    console.log('Login with new password status:', newLoginRes.status);
    if (newLoginRes.status !== 200) throw new Error('Login with new password failed');

    console.log('\n🎉 ALL BACKEND AUTH API TESTS PASSED SUCCESSFULLY! 🎉\n');
  } catch (err) {
    console.error('❌ Test failed:', err);
    process.exitCode = 1;
  } finally {
    server.close();
  }
});
