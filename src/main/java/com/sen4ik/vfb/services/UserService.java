package com.sen4ik.vfb.services;

import com.sen4ik.vfb.entities.User;
import com.sen4ik.vfb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User findByConfirmationToken(String confirmationToken) {
		return userRepository.findByConfirmationToken(confirmationToken);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

    public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User findById(Integer userId) {
		return userRepository.findById(userId).get();
	}

    public List<User> findAll() {
    	return userRepository.findAll();
	}

    public void editUser(User user) {
		userRepository.save(user);
    }
}
