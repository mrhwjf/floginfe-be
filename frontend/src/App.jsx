// App.js
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ProductDashboard from './components/ProductDashboard.jsx';

const PRODUCT_DASHBOARD_PATH = '/dashboard';

function App() {
	return (
		<Router>
			<Routes>
				<Route path={PRODUCT_DASHBOARD_PATH} element={
					<div style={{ padding: 16 }}>
						<ProductDashboard />
					</div>} />
			</Routes>
		</Router>
	);
}

export default App;