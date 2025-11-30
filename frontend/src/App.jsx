// App.js
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import ProductDashboard from './components/ProductDashboard.jsx';
import LoginForm from './components/Login/LoginForm.jsx';

const PRODUCT_DASHBOARD_PATH = '/dashboard';
const LOGIN_PATH = '/auth/login';

function App() {
	return (
		<Router>
			<Routes>

				{/* Default route → redirect to login */}
				<Route path="/" element={<Navigate to={LOGIN_PATH} replace />} />

				<Route
					path={PRODUCT_DASHBOARD_PATH}
					element={
						<div style={{ padding: 16 }}>
							<ProductDashboard />
						</div>
					}
				/>

				<Route path={LOGIN_PATH} element={<LoginForm />} />

			</Routes>
		</Router>
	);
}

export default App;