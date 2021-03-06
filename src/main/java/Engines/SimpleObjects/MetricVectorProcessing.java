package Engines.SimpleObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Engines.Distances.EuclideanDistance;
import Engines.KLDiv.KullbackLeiblerDivergenz;

/**
 * This class gather all informations for a the necessary vector type by a given TextInformations object.
 * It also provides functions to calculate distances between 2 vectors and allow further to rate there distance.
 * @author TTurke
 *
 */
public class MetricVectorProcessing 
{
	//epsilon
	static double epsilon = 0.00000001;
	
	//resource
	private String name;
	
	//Non_Gerbil metrics
	public HashMap<Integer, Double> symbol_sent_dist;
	public HashMap<Character, Double> symbol_error_dist;
	public HashMap<String, Double> syntactic_error_dist;
	public HashMap<Integer, Double> word_occurrence_dist;
	public HashMap<String, Double> pos_tags_dist;
	public HashMap<Integer, Double> annotated_entity_dist;
	
	//Gerbil metrics
	public HashMap<String, Double> gerbil_metrics;
	public ArrayList<Double> distanceVector;
	private ArrayList<Double> zero_vector;
	
	//##################################################################################
	//################################## CONSTRUCTOR ###################################
	//##################################################################################
	
	/**
	 * Constructor
	 * Need the count of all non GERBIL metrics
	 * @param insertion
	 * @param ngm_count
	 * @param gm_count
	 */
	public MetricVectorProcessing(TextInformations insertion, int ngm_count)
	{
		this.name = insertion.getResource_name();
		
		this.symbol_error_dist = insertion.getSymbol_error_dist();		/*M_1*/
		this.symbol_sent_dist = insertion.getSymbol_sent_dist();		/*M_2*/
		this.syntactic_error_dist = insertion.getSyn_error_dist();		/*M_3*/
		this.word_occurrence_dist = insertion.getWords_occurr_distr();	/*M_4*/
		this.pos_tags_dist = insertion.getPos_tags_dist();				/*M_5*/
		this.annotated_entity_dist = insertion.getAnnotation_dist();	/*M_6*/
		this.gerbil_metrics = insertion.getMetrics_GERBIL();			/*M_GERBIL*/
		
		//at the beginning empty
		distanceVector = new ArrayList<Double>();	
		
		// 10 value = 6 non-gerbil metrics + 4 gerbil metrics 
		zero_vector = new ArrayList<Double>(
				Collections.nCopies(ngm_count+insertion.getMetrics_GERBIL().keySet().size(), 1.0));
		
	}
	
	//##################################################################################
	//################################# USAGE METHODS ##################################
	//##################################################################################
	
	/*
	 * TODO FURTHER PROGRAMMING:
	 * implement separated list for non GERBIL metrics and the opposite, 
	 * because you can add easier add more metrics for both list 
	 * and calculate the distance vector later.
	 * => list1.add(list2); 
	 */
	/**
	 * This method allow tho generate the distance vector by 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static ArrayList<Double> calcDistanceVector(MetricVectorProcessing v1, MetricVectorProcessing v2)
	{
		ArrayList<Double> distance_vector = new ArrayList<Double>();
		double[] gm;
		double maximum;
		
		// Gerbil metrics (6 metrics currently) ATTENTION add more right here if you need them
		

//		System.out.println("\nM_1");
		distance_vector.add(SimpleRounding.round(KullbackLeiblerDivergenz.EasyKLDivergenceTD(v1.symbol_error_dist, v2.symbol_error_dist)));
//		System.out.println("\nM_2");
		distance_vector.add(SimpleRounding.round(KullbackLeiblerDivergenz.EasyKLDivergenceTD(v1.symbol_sent_dist, v2.symbol_sent_dist)));
//		System.out.println("\nM_3");
		distance_vector.add(SimpleRounding.round(KullbackLeiblerDivergenz.EasyKLDivergenceTD(v1.syntactic_error_dist, v2.syntactic_error_dist)));
//		System.out.println("\nM_4");
		distance_vector.add(SimpleRounding.round(KullbackLeiblerDivergenz.EasyKLDivergenceTD(v1.word_occurrence_dist, v2.word_occurrence_dist)));
//		System.out.println("\nM_5");
		distance_vector.add(SimpleRounding.round(KullbackLeiblerDivergenz.EasyKLDivergenceTD(v1.pos_tags_dist, v2.pos_tags_dist)));
//		System.out.println("\nM_6");
		distance_vector.add(SimpleRounding.round(KullbackLeiblerDivergenz.EasyKLDivergenceTD(v1.annotated_entity_dist, v2.annotated_entity_dist)));
		
		// Gerbil metrics (4 metrics currently)
		ArrayList<String> keys = new ArrayList<String>(v1.gerbil_metrics.keySet());
		
//		System.out.println("\nM_GERBIL");
		for (String key :  keys) 
		{
			gm = useOrRepalceDouble(v1.gerbil_metrics, v2.gerbil_metrics, key);
			distance_vector.add(SimpleRounding.round(EuclideanDistance.euclideanDistance(gm[0], gm[1])));
		}
		
		//Get maximum of the non GERBIL metrics and Normalize them with the maximum
		maximum = getMaxValue(distance_vector);
		for(int vv = 0; vv < distance_vector.size(); vv++) distance_vector.set(vv, (distance_vector.get(vv)/maximum));
		
		return distance_vector;
	}
	
	/*
	 * TODO FURTHER PROGRAMMING:
	 * Find a way to merge useOrRepalceDouble and useOrRepalceInteger class,
	 * because they are similar in general.
	 */
	/**
	 * This method check 2 maps about a key is existing and return there values, 
	 * and if some map hasn't the key it set a zero for it.
	 * @param m1
	 * @param m2
	 * @param key
	 * @return 2 decimal
	 */
	public static double[] useOrRepalceDouble(Map<String, Double> m1, Map<String, Double> m2, String key)
	{
		if(key != null)
		{
			double[] output = new double[2];
			
			
			//Check key existence and set value for m1
			if(m1.containsKey(key))
			{
				output[0] = m1.get(key);
			}else{
				output[0] = epsilon;
			}
			
			//Check key existence and set value for m2
			if(m2.containsKey(key))
			{
				output[1] = m2.get(key);
			}else{
				output[1] = epsilon;
			}
			
			return output;
		}else{
			return null;
		}
	}
	
	/**
	 * This method check 2 maps about a key is existing and return there values, 
	 * and if some map hasn't the key it set a zero for it.
	 * @param m1
	 * @param m2
	 * @param key
	 * @return 2 integers
	 */
	public static int[] useOrRepalceInteger(Map<String, Integer> m1, Map<String, Integer> m2, String key)
	{
		if(key != null)
		{
			int[] output = new int[2];
			
			
			//Check key existence and set value for m1
			if(m1.containsKey(key))
			{
				output[0] = m1.get(key);
			}else{
				output[0] = 0;
			}
			
			//Check key existence and set value for m2
			if(m2.containsKey(key))
			{
				output[1] = m2.get(key);
			}else{
				output[1] = 0;
			}
			
			return output;
		}else{
			return null;
		}
	}
	
	/**
	 * This method calculate the arithmetical mean of a distance vector.
	 * @param dist_vec
	 * @return rating decimal value
	 */
	public static double rate(ArrayList<Double> dist_vec)
	{
		if(dist_vec.isEmpty())
		{
			return Double.NaN;
		}
		
		double ratingNGM = 0.0, ratingGM = 0.0;
		
		//arithmetic mean for M1-M6 (subratings)
		for(int ngms = 0; ngms < 6; ngms++){
			ratingNGM += dist_vec.get(ngms); 
		}
		
		ratingNGM = ratingNGM / 6;
		System.out.println("Subrating NGM: "+ratingNGM);
		
		//arithmetic mean for all GERBIL-metrics (subratings)
		for(int ngms = 6; ngms < dist_vec.size(); ngms++){
			ratingGM += dist_vec.get(ngms); 
		}
		
		ratingGM = ratingGM / (dist_vec.size()-6);
		System.out.println("Subrating GM: "+ratingGM);
		
		//arithmetic mean over subratings
		return ((ratingGM+ratingNGM)/2);
	}
	
	/**
	 * Get the maximum value of a list of doubles
	 * @param ds
	 * @return maximum
	 */
	public static double getMaxValue(ArrayList<Double> ds)
	{
		double max=Double.MIN_VALUE;
		for (double d : ds) if (d > max) max=d;
		return max;
	}
	
	/**
	 * Get the minimum value of a list of doubles
	 * @param ds
	 * @return minimum
	 */
	public static double getMinValue(ArrayList<Double> ds)
	{
		double min=Double.MAX_VALUE;
		for (double d : ds) if (d < min ) min=d;
		return min;
	}
	
	
	//##################################################################################
	//############################## GETTERS AND SETTERS ###############################
	//##################################################################################
	
	public ArrayList<Double> getZero_vector() {
		return zero_vector;
	}
	
	public String getName() {
		return name;
	}
}
