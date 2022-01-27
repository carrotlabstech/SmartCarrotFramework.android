package com.carrotlabs.smartcarrotframework

/**
 * Defines the model type used for categorisation.
 */
public enum class CategorisationType {
    /**
     * The Optimised Native model will be used for categorisation. Personalised user's learning won't be taken into consideration.
     */
    nonPersonal,

    /**
     * The Personalised Model will be used for categorisation.
     */
    personal
}