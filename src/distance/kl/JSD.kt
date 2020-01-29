package distance.kl

import distance.pattern.PatternDistribution


// https://en.wikipedia.org/wiki/Jensen%E2%80%93Shannon_divergence


class JSD {
    fun div(p: PatternDistribution, q: PatternDistribution, w: Double = 0.5) : Double {

        // the PatternDistributions store raw counts of the number of times each pattern occurred
        // probabilities are calculated at the point of usage
        // the mixture distribution 'm' adds the raw figures together for p and q
        // hence more weight is placed on distributions that have more samples in them

        // this might be a good or bad thing, but this is how it is at the moment

        val m = PatternDistribution()
        m.add(p)
        m.add(q)

        return w * KLDiv.klDiv(p, m) + (1-w) * KLDiv.klDiv(q,m)

    }
}

