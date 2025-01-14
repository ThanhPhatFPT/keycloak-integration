import React, { useState } from "react";
import axios from "axios";
import { jwtDecode } from "jwt-decode";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await axios.post("http://localhost:8080/profile/login", {
                username,
                password,
            });

            const { result } = response.data;

            // Lưu token vào localStorage
            localStorage.setItem("token", result);

            // Giải mã token để lấy profileId
            const decodedToken = jwtDecode(result);
            const profileId = decodedToken.sub || decodedToken.sub; // Lấy profileId từ token (hoặc sub nếu đó là key của profileId)

            // Log profileId ra console
            console.log("Logged in with profileId:", profileId);

            // Hiển thị thông báo đăng nhập thành công
            alert("Login successful!");

            // Chuyển hướng người dùng đến trang dashboard hoặc home
            window.location.href = "/profile"; // Thay bằng route mong muốn
        } catch (err) {
            console.error(err);
            setError(
                err.response?.data?.message || "An error occurred during login."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="w-full max-w-md p-8 bg-white rounded shadow-md">
                <h2 className="text-2xl font-bold text-center mb-6">Login</h2>
                <form onSubmit={handleLogin} className="space-y-4">
                    <div>
                        <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                            Username
                        </label>
                        <input
                            id="username"
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                            required
                        />
                    </div>
                    <div>
                        <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                            required
                        />
                    </div>
                    {error && <p className="text-red-500 text-sm">{error}</p>}
                    <div>
                        <button
                            type="submit"
                            className="w-full bg-indigo-600 text-white py-2 px-4 rounded hover:bg-indigo-700 focus:outline-none focus:bg-indigo-700"
                            disabled={loading}
                        >
                            {loading ? "Logging in..." : "Login"}
                        </button>
                    </div>
                </form>
                <p className="text-sm text-gray-600 mt-4 text-center">
                    Don't have an account?{" "}
                    <a href="/register" className="text-indigo-600 hover:underline">
                        Register here
                    </a>
                </p>
            </div>
        </div>
    );
};

export default Login;
