const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET || 'homehelp_secret_jwt_key_hyderabad_2026_xyz';

const authMiddleware = (req, res, next) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({
      success: false,
      message: 'Unauthorized: Authentication token is missing. Please log in.',
    });
  }

  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    req.customer = decoded;
    next();
  } catch (err) {
    return res.status(401).json({
      success: false,
      message: 'Unauthorized: Invalid or expired session token. Please log in again.',
    });
  }
};

module.exports = {
  authMiddleware,
  JWT_SECRET,
};
