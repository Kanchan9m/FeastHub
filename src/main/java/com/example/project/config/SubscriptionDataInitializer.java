package com.example.project.config;

import com.example.project.model.PlanType;
import com.example.project.model.SubscriptionPlan;
import com.example.project.repositories.SubscriptionPlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriptionDataInitializer {

    @Bean
    CommandLineRunner initializeSubscriptionPlans(
            SubscriptionPlanRepository subscriptionPlanRepository) {

        return args -> {

            if (subscriptionPlanRepository.count() == 0) {

                SubscriptionPlan free = new SubscriptionPlan();
                free.setPlanType(PlanType.FREE);
                free.setPlanName("Free");
                free.setRestaurantLimit(3);
                free.setPrice(0.0);
                free.setDescription("Manage up to 3 restaurants");

                subscriptionPlanRepository.save(free);


                SubscriptionPlan premium = new SubscriptionPlan();
                premium.setPlanType(PlanType.PREMIUM);
                premium.setPlanName("Premium");
                premium.setRestaurantLimit(10);
                premium.setPrice(999.0);
                premium.setDescription("Manage up to 10 restaurants");

                subscriptionPlanRepository.save(premium);


                SubscriptionPlan enterprise = new SubscriptionPlan();
                enterprise.setPlanType(PlanType.ENTERPRISE);
                enterprise.setPlanName("Enterprise");
                enterprise.setRestaurantLimit(-1);
                enterprise.setPrice(0.0);
                enterprise.setDescription("Unlimited restaurants");

                subscriptionPlanRepository.save(enterprise);

                System.out.println("Subscription plans initialized successfully.");
            }
        };
    }
}
